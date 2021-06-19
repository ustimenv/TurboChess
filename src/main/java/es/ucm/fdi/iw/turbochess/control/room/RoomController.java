package es.ucm.fdi.iw.turbochess.control.room;


import es.ucm.fdi.iw.turbochess.model.User;
import es.ucm.fdi.iw.turbochess.model.messaging.MessagePacket;
import es.ucm.fdi.iw.turbochess.model.messaging.ResponsePacket;
import es.ucm.fdi.iw.turbochess.model.room.Participant;
import es.ucm.fdi.iw.turbochess.model.room.Room;
import es.ucm.fdi.iw.turbochess.service.room.RoomException;
import es.ucm.fdi.iw.turbochess.service.room.RoomService;
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

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;

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


	@MessageMapping("/{room}.chat.betRaise")
	@SendTo("/queue/{room}")
	public MessagePacket betRaise(@DestinationVariable String room, @Payload MessagePacket messagePacket) {
		log.info(room);
		if(room == null){
			log.error("[betRaise]: room is null!");
		} else{
			log.info(format("betRaise]: bet msg{0} sent successfully", messagePacket));
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


// AJAX
	@RequestMapping(value = "/api/createroom", method=RequestMethod.POST, produces = "application/json")
	@ResponseBody
	@Transactional
	public ResponsePacket createRoom(@RequestBody MessagePacket packet){
		log.info(format("[create room]: received packet{0}", packet));
		String from = packet.getFrom();
		String code = nextRoomCode();					// code generation guarantees room code is unique

		try{
			User userFrom = getUserByUsername(from);
			Room createdRoom = roomService.createRoom(code, Integer.parseInt(packet.getPayload()));
			Participant p = new Participant(createdRoom, userFrom);
			p.setRole(roomService.assignRole(code, p));
			entityManager.persist(p);
			log.info(format("Room {0} created successfully by {1}", code, p.getUser().getUsername()));
			return new ResponsePacket(code);
		} catch(RoomException e){
			log.error(format("[create room]: failed to create room {0}", e.getMessage()));
			return null;		// TODO change to 505 or smth
		}
	}

	@RequestMapping(value = "/api/joinroom", method=RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponsePacket joinRoom(@RequestBody MessagePacket packet){
		log.info(format("[join room]: received packet{0}", packet));
		String from = packet.getFrom();
		String code = packet.getPayload();

		try{
			User userFrom = getUserByUsername(from);
			Room roomToJoin = roomService.getRoomByCode(code);
			Participant p = new Participant(roomToJoin, userFrom);
			p.setRole(roomService.assignRole(code, p));
			roomService.joinRoom(code, p);
			log.info(format("User {0} joined room {1} successfully", from, code));
			return new ResponsePacket("oki");
		} catch(RoomException e){
			log.error(format("[join room]: User {0}failed to join room {1} \n{2}", from, code, e.getMessage()));
			return null;
		}
	}

	private User getUserByUsername(String username) throws RoomException{
		Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username")
									.setParameter("username", username);
		User u = (User) query.getSingleResult();
		if(u == null){
			throw new RoomException(format("Unable to find user {0}", username));
		} else	return u;
	}

}