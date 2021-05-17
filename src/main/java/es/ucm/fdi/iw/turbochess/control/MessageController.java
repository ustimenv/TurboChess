package es.ucm.fdi.iw.turbochess.control;



import es.ucm.fdi.iw.turbochess.model.MessagePacket;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;



@Controller
public class MessageController {
	//	private static final Logger log = LogManager.getLogger(MessageController.class);

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




}