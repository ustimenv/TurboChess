package turbochess.control.room;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.jayway.jsonpath.JsonPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import turbochess.model.User;
import turbochess.model.messaging.MessagePacket;
import turbochess.model.messaging.ResponsePacket;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;
import turbochess.service.room.RoomException;
import turbochess.service.room.RoomService;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;

import static com.fasterxml.jackson.databind.DeserializationFeature.UNWRAP_ROOT_VALUE;
import static java.text.MessageFormat.format;

@Controller
public class RoomController{
	private static Logger log = LogManager.getLogger(User.class);

	private int n1=0, n2=0, n3=0;		// counters for the 3 segments that form the room code
	private char[] letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	private ArrayList<String> segments = new ArrayList<>();
	private int len;

	@PostConstruct
	private void initCodeSegments(){
		for(char c1 : letters){
			for(char c2 : letters){
				segments.add(String.valueOf(c1) + c2);
			}
		}
		len=segments.size();
	}

	// THIS WORKS, DON'T TOUCH!!
	private synchronized String nextRoomCode(){
		String code=segments.get(n1) + segments.get(n2) + segments.get(n3);
		if(n3-1>len){
			if(n2-1>len){
				if(n1-1>len){
					log.info("Room code overflow");
					n1=0;
				} else n1++;
				n2=0;
			} else n2++;
			n3=0;
		} else n3++;
		return code;
	}



	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	private RoomService roomService;

	@Autowired
	private EntityManager entityManager;		// instead we could potentially convert the User into a repository

	//STOMP
	@MessageMapping("/{room}.chat.sendMessage")
	@SendTo("/queue/{room}")
	public MessagePacket sendMessage(@DestinationVariable String room, @Payload MessagePacket messagePacket) {
		log.info(room);
		if(room == null){
			log.error("[sendMessage]: room is null!");
		} else{
			log.info(format("[sendMessage]: message{0} sent successfully", messagePacket));
		}
		return messagePacket;
	}

	@MessageMapping("/{room}.chat.addUser")
	@SendTo("/queue/{room}")
	public MessagePacket addUser(@DestinationVariable String room, @Payload MessagePacket messagePacket,
								 SimpMessageHeaderAccessor headerAccessor) {
		log.info(room);
		// Add username in web socket session
		if(room == null){
			log.error("[addUser]: room is null!");
		} else{
			log.info(format("[addUser]: joining msg{0} sent successfully", messagePacket));
		}
		headerAccessor.getSessionAttributes().put("username", messagePacket.getFrom());
		return messagePacket;
	}

	@MessageMapping("/{room}.sys.makeMove")
	@SendTo("/queue/{room}")
	public MessagePacket makeMove(@DestinationVariable String room, @Payload MessagePacket messagePacket) {
		log.info(format("Room [{0}]: move made-->{1}", room, messagePacket));

		if(room == null || messagePacket==null)	return null;
		for(int i=0; i<10; i++)
			System.out.println("Woop woop");
		// check to see if the sender has the permission to make this move
		String from=null, payload=null;
		try{
			from = messagePacket.getFrom();
			payload = messagePacket.getPayload();
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode node = objectMapper.readValue(payload, ObjectNode.class);
			String allegedColour = String.valueOf(node.get("color")).replaceAll("\"", "");

			System.out.println(allegedColour);
			System.out.println(allegedColour.getClass());
			System.out.println(allegedColour.hashCode());

			User user = getUserByUsername(from);
			Room contextRoom = roomService.getRoomByCode(room);
			Participant p = getParticipantByUsernameAndRoom(user, contextRoom);

			System.out.println(p.getColourString());
			System.out.println(p.getColourString().getClass());
			System.out.println(p.getColourString().hashCode());

			if(!p.getColourString().equals(allegedColour))
				throw new RoomException(format("Participant {0} with colour {1} can't move for {2}", from, p.getColourString(), allegedColour));
			log.info(format("User {0} made a move successfully", from));
			log.info(format("User {0} made a move successfully", from));
			log.info(format("User {0} made a move successfully", from));
			log.info(format("User {0} made a move successfully", from));
			log.info(format("User {0} made a move successfully", from));
			log.info(format("User {0} made a move successfully", from));
			return messagePacket;
		} catch(RoomException | JsonProcessingException e){
			log.error(format("[make move]: User {0} -->", from, e.getStackTrace()));
			e.printStackTrace();
			return null;
		}
	}

// AJAX
	@RequestMapping(value = "/api/create_room", method=RequestMethod.POST, produces = "application/json")
	@ResponseBody
	@Transactional
	public ResponsePacket createRoom(@RequestBody MessagePacket packet){
		log.info(format("[create room]: received packet{0}", packet));
		try{
			String from = packet.getFrom();
			String code = nextRoomCode();					// code generation guarantees room code is unique
			User userFrom = getUserByUsername(from);
			Room createdRoom = roomService.createRoom(code, Integer.parseInt(packet.getPayload()));
			Participant p = new Participant(createdRoom, userFrom);
			p.setRole(roomService.assignRole(code, p));
			p.setColour(Participant.Colour.WHITE);
			roomService.joinRoom(createdRoom.getCode(), p);
			entityManager.persist(p);
			log.info(format("Room {0} created successfully by {1}", code, p.getUser().getUsername()));
			return new ResponsePacket(p.getColourString(), code);
		} catch(RoomException e){
			log.error(format("[create room]: failed to create room {0}", e.getMessage()));
			return null;		// TODO change to 505 or smth
		}
	}

	@RequestMapping(value = "/api/join_room", method=RequestMethod.POST, produces = "application/json")
	@ResponseBody
	@Transactional
	public ResponsePacket joinRoom(@RequestBody MessagePacket packet){
		log.info(format("[join room]: received packet{0}", packet));
		String from=null, code=null;
		try{
			from = packet.getFrom();
			code = packet.getPayload();
			User userFrom = getUserByUsername(from);
			if(!roomService.isRoomBelowCapacity(code))	throw new RoomException(format("Capacity exceeded for room {0)", code));

			Room roomToJoin = roomService.getRoomByCode(code);
			Participant p = new Participant(roomToJoin, userFrom);
			p.setRole(roomService.assignRole(code, p));
			if(p.getRole() == Participant.Role.OBSERVER){
				p.setColour(Participant.Colour.NONE);
			} else{
				p.setColour(Participant.Colour.BLACK);
			}
			roomService.joinRoom(code, p);
			entityManager.persist(p);
			log.info(format("User {0} joined room {1} successfully", from, code));
			return new ResponsePacket(p.getColourString(), "oki");
		} catch(RoomException e){
			log.error(format("[join room]: User {0}failed to join room {1} \n{2}", from, code, e.getMessage()));
			return null;
		}
	}

//	@RequestMapping(value = "/api/place_bet", method=RequestMethod.POST, produces = "application/json")
//	@ResponseBody
//	@Transactional
//	public ResponsePacket placeBet(@RequestBody MessagePacket packet){
//		log.info(format("[place bet]: received packet{0}", packet));
//		String from =null;
//		int betAmount=-1;
//		try{
//			from = packet.getFrom();
//			betAmount = Integer.parseInt(packet.getPayload());
//			if(betAmount<=0)	throw new NumberFormatException("Invalid bet value: " + betAmount);
//			User userFrom = getUserByUsername(from);
//			Integer currentUserBalance = entityManager.createNamedQuery("User.getBalanceByUsername", Integer.class)
//					.setParameter("username", userFrom.getUsername())
//					.getSingleResult();
//
//			Room room = roomService.getRoomByCode(packet.getContext());
//			Participant p = getParticipantByUsernameAndRoom(userFrom, room);
//			// all the relevant variables initialised at this point
//			if(currentUserBalance == null || currentUserBalance < betAmount){
//				throw new RoomException(format("Insufficient ({0}) balance ({1}) for player {2}", currentUserBalance, betAmount, userFrom.getUsername()));
//			}
//			entityManager.createNamedQuery("User.removeCoins")
//			 			 .setParameter("username", userFrom.getUsername())
//						 .setParameter("amount", betAmount).executeUpdate();
//
//			log.info(format("Bet of {0} coins has been placed successfully by {1}", betAmount, p.getUser().getUsername()));
//			return new ResponsePacket("OKI");
//		} catch(RoomException | NumberFormatException e){
//			log.error(format("[place bet]: failed to place bet for participant {0} --> {1}", from, e.getMessage()));
//			return null;		// TODO change to 505 or smth
//		}
//	}


	private User getUserByUsername(String username) throws RoomException{
		Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username")
									.setParameter("username", username);
		User u = (User) query.getSingleResult();
		if(u == null){
			throw new RoomException(format("Unable to find user {0}", username));
		} else	return u;
	}
	private Participant getParticipantByUsernameAndRoom(User user, Room room) throws RoomException{
		String username, roomCode;
		try{
			username = user.getUsername();
			roomCode = room.getCode();
			Participant p = (Participant) entityManager.createNativeQuery(
					"SELECT * FROM Participant WHERE user_id = :user_id AND room_code = :code",  Participant.class)
					.setParameter("user_id", user.getId()).setParameter("code", room.getCode()).getSingleResult();
			if(p == null){
				throw new RoomException(format("Unable to find participant {0} in room {1}", username, roomCode));
			} else	return p;
		} catch(Exception e){
			throw new RoomException(e.getMessage());
		}
	}
}