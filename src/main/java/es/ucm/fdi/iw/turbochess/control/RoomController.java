package es.ucm.fdi.iw.turbochess.control;



import es.ucm.fdi.iw.turbochess.model.Participant;
import es.ucm.fdi.iw.turbochess.model.Room;
import es.ucm.fdi.iw.turbochess.model.RoomPOJO;
import es.ucm.fdi.iw.turbochess.model.User;
import es.ucm.fdi.iw.turbochess.model.messaging.MessagePacket;
import es.ucm.fdi.iw.turbochess.model.messaging.ResponsePacket;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


@Controller
public class RoomController{
	Map<String, Room> rooms = new HashMap<>();


	//STOMP
	@MessageMapping("/chat.sendMessage")
	@SendTo("/queue/public")
	public MessagePacket sendMessage(@Payload MessagePacket messagePacket) {
		return messagePacket;
	}

	@MessageMapping("/chat.betRaise")
	@SendTo("/queue/public")
	public MessagePacket betRaise(@Payload MessagePacket messagePacket) {
		return messagePacket;
	}


	@MessageMapping("/chat.addUser")
	@SendTo("/queue/public")
	public MessagePacket addUser(@Payload MessagePacket messagePacket,
								 SimpMessageHeaderAccessor headerAccessor) {
		// Add username in web socket session
		headerAccessor.getSessionAttributes().put("username", messagePacket.getFrom());
		return messagePacket;
	}

//	@MessageMapping("/chat.addUser")
//	@SendTo("/queue/public")
//	public MessagePacket addUser(@Payload MessagePacket messagePacket,
//								 SimpMessageHeaderAccessor headerAccessor) {
//		// Add username in web socket session
//		headerAccessor.getSessionAttributes().put("username", messagePacket.getFrom());
//		return messagePacket;
//	}


	// AJAX
	@RequestMapping(value = "/api/createroom", method=RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponsePacket createRoom(@RequestBody MessagePacket packet) {

		String code = SessionKeeper.INSTANCE.generateRoomCode();
		//TODO SQL query to get user by username


//		Room r = new Room(code);
//		r.addParticipant(new Participant());
//		rooms.add(new Room(code));

		ResponsePacket response = new ResponsePacket();
		response.setPayload(code);
		return response;
	}




}