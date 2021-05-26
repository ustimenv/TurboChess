package es.ucm.fdi.iw.turbochess.control;


import es.ucm.fdi.iw.turbochess.model.Participant;
import es.ucm.fdi.iw.turbochess.model.Room;
import es.ucm.fdi.iw.turbochess.model.User;
import es.ucm.fdi.iw.turbochess.model.messaging.MessagePacket;
import es.ucm.fdi.iw.turbochess.model.messaging.ResponsePacket;
import es.ucm.fdi.iw.turbochess.service.RoomException;
import es.ucm.fdi.iw.turbochess.service.RoomService;
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

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

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
	private static Logger log = LogManager.getLogger(User.class);

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
	public ResponsePacket createRoom(@RequestBody MessagePacket packet){		// todo handle the exception!!
		log.info("[create room]: received packet" + packet);
		int maxAttemptsToGenerateCode=10;
		String code=null;
		String from = packet.getFrom();		// todo sanitise!!!!!
		int i=0;
		while(roomService.roomExists(code)){
			code = CodeGenerator.INSTANCE.generateRoomCode();
			i++;
			if(i > maxAttemptsToGenerateCode){
				log.error("[create room]: failed to create room");
				return null;	// basically a 500
			}
		}

		try{
			Room r = roomService.createRoom(code, Integer.parseInt(packet.getPayload()));
			Participant p = getParticipantByUsername(from);
			roomService.joinRoom(code, p);
			log.info("Room " + r.getCode() + " created successfully");
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
		Query query = entityManager.createQuery("SELECT * FROM user u WHERE u.username = :username")
									.setParameter("username", username);

		return new Participant((User) query.getSingleResult());	// todo ought to add null check
	}
}