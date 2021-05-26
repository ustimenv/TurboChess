package es.ucm.fdi.iw.turbochess.control;


import es.ucm.fdi.iw.turbochess.model.Participant;
import es.ucm.fdi.iw.turbochess.model.Room;
import es.ucm.fdi.iw.turbochess.model.User;
import es.ucm.fdi.iw.turbochess.model.messaging.MessagePacket;
import es.ucm.fdi.iw.turbochess.model.messaging.ResponsePacket;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//TODO below::::
/**	In order to provide a common return type for the handlers, clean up the code, and most importantly handle exceptions,
 * we will change the return types of STOMP handlers from MessagePacket to void (it is unrelated but should be done),
 * utilising template.convertAndSend();
 * Response packet will be changed to ResponseEntity and another class, @ControllerAdvice, will handle the exceptions raised
 *
 * eg
 * https://www.toptal.com/java/spring-boot-rest-api-error-handling
 * https://stackoverflow.com/questions/38117717/what-is-the-best-way-to-return-different-types-of-responseentity-in-spring-mvc-o
 */

@Controller
public class RoomController{
	Set<Room> rooms = new HashSet<>();
	private static Logger log = LogManager.getLogger(User.class);

	@Autowired
	private SimpMessagingTemplate template;

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
	public ResponsePacket createRoom(@RequestBody MessagePacket packet) {
		log.info("[create room]: received packet" + packet);
		String code = SessionKeeper.INSTANCE.generateRoomCode();	// todo this needs to be refactored
		int capacity = Integer.parseInt(packet.getPayload());
//		Room r = new Room(code, capacity);
		Room r = new Room(code, 5);

		r.addParticipant(new Participant());	//TODO SQL query to get user by username
		if(rooms.contains(r)){
			log.error("Attempting to create room " + r.getCode() + " already exists!");
			return null;						//TODO not sure whether to return null or not-okay
		}
		rooms.add(r);
		log.info("Room " + r.getCode() + " created successfully");
		return new ResponsePacket(code);
	}

	@RequestMapping(value = "/api/joinroom", method=RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponsePacket joinRoom(@RequestBody MessagePacket packet) {
		log.info("[join room]: received packet" + packet);
		rooms.add(new Room("X", 3));		//____________ for testing ONLY!!___________//TODO delete!________________________________________________
		String code = packet.getPayload();
		String from = packet.getFrom();
		for(Room room : rooms){
			if(room.getCode().equals(code)){
				room.addParticipant(new Participant());
				log.info("User " + from + "joined room " + room.getCode() + " successfully");
				return new ResponsePacket("oki");
			}
		}
		log.error("Attempting to create room " + code + " but it already exists!");
		return null;
	}
}