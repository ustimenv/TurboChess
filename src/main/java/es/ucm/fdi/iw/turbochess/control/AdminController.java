package es.ucm.fdi.iw.turbochess.control;
import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import es.ucm.fdi.iw.turbochess.model.Friendship;
import es.ucm.fdi.iw.turbochess.repository.FriendshipRepository;
import es.ucm.fdi.iw.turbochess.service.FriendshipService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.iw.turbochess.LocalData;
import es.ucm.fdi.iw.turbochess.model.User;

/**
 * Admin-only controller
 * @author mfreire
 */
@Controller()
@RequestMapping("admin")
public class AdminController {
	
	 private static final Logger log = LogManager.getLogger(AdminController.class);
	
	 @Autowired
	 private EntityManager entityManager;

    @Autowired
    private FriendshipService friendshipservice;
	
	 @Autowired
	 private LocalData localData;
	
	 @Autowired
	 private Environment env;
	
	 @GetMapping("")
	 public String index(Model model) {
		 List<User> users = entityManager.createNamedQuery("User.findAll").getResultList();
		 model.addAttribute("usersList", users);
	 	model.addAttribute("activeProfiles", env.getActiveProfiles());
	 	model.addAttribute("basePath", env.getProperty("es.ucm.fdi.base-path"));
	 	model.addAttribute("debug", env.getProperty("es.ucm.fdi.debug"));

	 //	model.addAttribute("users", entityManager.createQuery("SELECT u FROM User u").getResultList());
		 model.addAttribute("users",users);
	 	return "admin";
	 }
	
	 @PostMapping("/toggleuser")
	 @Transactional
	 public String delUser(Model model,	@RequestParam long id) {
	 	User target = entityManager.find(User.class, id);
	 	if (target.getEnabled() == 1) {
	 		// remove profile photo
	 		File f = localData.getFile("user", ""+id);
	 		if (f.exists()) {
	 			f.delete();
	 		}
	 		// disable user
	 		target.setEnabled((byte)0);
	 	} else {
	 		// enable user
	 		target.setEnabled((byte)1);
		}
	 	return "redirect:/admin";
	 }
}