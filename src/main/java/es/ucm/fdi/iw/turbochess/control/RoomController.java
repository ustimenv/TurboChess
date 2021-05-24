package es.ucm.fdi.iw.turbochess.control;


import es.ucm.fdi.iw.turbochess.model.Participant;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Controller
public class RoomController{
	Set<Room> rooms = new HashSet<>();

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
		String code = SessionKeeper.INSTANCE.generateRoomCode();	// todo this needs to be refactored
		int capacity = Integer.parseInt(packet.getPayload());
		Room r = new Room(code, capacity);

		r.addParticipant(new Participant());	//TODO SQL query to get user by username
		if(rooms.contains(r)){
			return null;						//TODO not sure whether to return null or not-okay
		}
		rooms.add(r);
		return new ResponsePacket(code);
	}

	@RequestMapping(value = "/api/joinroom", method=RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponsePacket joinRoom(@RequestBody MessagePacket packet) {
		rooms.add(new Room("X", 3));		//____________ for testing ONLY!!___________________________________________________________
		String code = packet.getPayload();
		for(Room room : rooms){
			if(room.getCode().equals(code)){
				room.addParticipant(new Participant());
				return new ResponsePacket("oki");
			}
		}
		return null;
	}
}