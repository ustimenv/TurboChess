package es.ucm.fdi.iw.turbochess.control;


import java.security.Principal;

import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import es.ucm.fdi.iw.turbochess.model.Message;


@Controller()
public class MessageController {	
	private static final Logger log = LogManager.getLogger(MessageController.class);

	@Autowired private SimpMessagingTemplate template;
	
	@MessageMapping("/hello")
	@SendTo("/queue/chat")
  	public String greeting(Object foo) throws Exception {
    	Thread.sleep(1000); 
    	return "Hello";
  	}

	  


	// @GetMapping("/")
	// public String getMessages(Model model, HttpSession session) {
	// 	model.addAttribute("users", entityManager.createQuery(
	// 		"SELECT u FROM User u").getResultList());
	// 	return "messages";
	// }

	// @GetMapping(path = "/received", produces = "application/json")
	// @Transactional // para no recibir resultados inconsistentes
	// @ResponseBody  // para indicar que no devuelve vista, sino un objeto (jsonizado)
	// public List<Message.Transfer> retrieveMessages(HttpSession session) {
	// 	long userId = ((User)session.getAttribute("u")).getId();		
	// 	User u = entityManager.find(User.class, userId);
	// 	log.info("Generating message list for user {} ({} messages)", 
	// 			u.getUsername(), u.getReceived().size());
	// 	return  u.getReceived().stream().map(Transferable::toTransfer).collect(Collectors.toList());
	// }	
	
	// @GetMapping(path = "/unread", produces = "application/json")
	// @ResponseBody
	// public String checkUnread(HttpSession session) {
	// 	long userId = ((User)session.getAttribute("u")).getId();		
	// 	long unread = entityManager.createNamedQuery("Message.countUnread", Long.class)
	// 		.setParameter("userId", userId)
	// 		.getSingleResult();
	// 	session.setAttribute("unread", unread);
	// 	return "{\"unread\": " + unread + "}";
	// }
}