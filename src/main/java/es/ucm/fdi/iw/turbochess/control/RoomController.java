package es.ucm.fdi.iw.turbochess.control;


import es.ucm.fdi.iw.turbochess.model.Room;
import es.ucm.fdi.iw.turbochess.model.messaging.MessagePacket;
import es.ucm.fdi.iw.turbochess.model.messaging.ResponsePacket;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;


@Controller
public class RoomController{
	Map<String, Room> rooms = new HashMap<>();

//STOMP
	@MessageMapping("/{room}.chat.sendMessage")
	@SendTo("/queue/{room}")
	public MessagePacket sendMessage(@DestinationVariable String room, @Payload MessagePacket messagePacket) {
		return messagePacket;
	}


	@MessageMapping("/{room}.chat.betRaise")
	@SendTo("/queue/{room}")
	public MessagePacket betRaise(@DestinationVariable String room, @Payload MessagePacket messagePacket) {
		return messagePacket;
	}


	@MessageMapping("/{room}.chat.addUser")
	@SendTo("/queue/{room}")
	public MessagePacket addUser(@DestinationVariable String room, @Payload MessagePacket messagePacket,
								 SimpMessageHeaderAccessor headerAccessor) {
		// Add username in web socket session
		headerAccessor.getSessionAttributes().put("username", messagePacket.getFrom());
		return messagePacket;
	}


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
//		response.setPayload(code);
		response.setPayload(code);

		return response;
	}




}