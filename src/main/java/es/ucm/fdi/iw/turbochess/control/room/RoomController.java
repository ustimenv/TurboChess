package es.ucm.fdi.iw.turbochess.control.room;


import es.ucm.fdi.iw.turbochess.model.User;
import es.ucm.fdi.iw.turbochess.model.messaging.MessagePacket;
import es.ucm.fdi.iw.turbochess.model.messaging.ResponsePacket;
import es.ucm.fdi.iw.turbochess.model.room.Participant;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;

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
			log.error("[sendMessage]: " + " room is null!");
		} else{
			log.info("[sendMessage]: message" +messagePacket + " sent successfully");
		}
		return messagePacket;
	}


	@MessageMapping("/{room}.chat.betRaise")
	@SendTo("/queue/{room}")
	public MessagePacket betRaise(@DestinationVariable String room, @Payload MessagePacket messagePacket) {
		log.info(room);
		if(room == null){
			log.error("[betRaise]: " + " room is null!");
		} else{
			log.info("betRaise]: bet msg" +messagePacket + " sent successfully");
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
			log.error("[addUser]: " + " room is null!");
		} else{
			log.info("[addUser]: joining msg" +messagePacket + " sent successfully");
		}
		headerAccessor.getSessionAttributes().put("username", messagePacket.getFrom());
		return messagePacket;
	}


// AJAX
	@RequestMapping(value = "/api/createroom", method=RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponsePacket createRoom(@RequestBody MessagePacket packet){
		log.info("[create room]: received packet" + packet);
		String from = packet.getFrom();
		String code=nextRoomCode();	// code generation guarantees room code is unique

		try{
			roomService.createRoom(code, Integer.parseInt(packet.getPayload()));
			Participant p = getParticipantByUsername(from);
			roomService.joinRoom(code, p);
			log.info("Room " + code + " created successfully");
			return new ResponsePacket(code);
		} catch(RoomException e){
			e.printStackTrace();
			log.error("[create room]: failed to create room " + e.getMessage());
			return null;
		}
	}

	@RequestMapping(value = "/api/joinroom", method=RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponsePacket joinRoom(@RequestBody MessagePacket packet){
		log.info("[join room]: received packet" + packet);

		String code = packet.getPayload();
		String from = packet.getFrom();
		Participant p = getParticipantByUsername(from);

		try{
			roomService.joinRoom(code, p);
			log.info("User " + from + "joined room " + code + " successfully");
			return new ResponsePacket("oki");
		} catch(RoomException e){
			log.error("[create room]: failed to create room " + code + " \n"+ e.getMessage());
			return null;
		}
	}

	private Participant getParticipantByUsername(String username){
		Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username")
									.setParameter("username", username);

//		Participant p = new Participant((User) query.getSingleResult());
//		System.out.println(p.getUser().getUsername());
		return null;
	}

}