package es.ucm.fdi.iw.turbochess.control;
import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import es.ucm.fdi.iw.turbochess.service.friendship.FriendshipService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "toggleuser")
	@Transactional
	public String toggleuser(Model model, @RequestParam long id, User user) {
		User target = entityManager.find(User.class, id);

		if (target.getEnabled() == 1) {
			// remove profile photo
			//File f = localData.getFile("user", ""+id);
			//if (f.exists()) {
			//	f.delete();
			//}
			// disable user
			target.setEnabled((byte) 0);
		} else {
			// enable user
			target.setEnabled((byte) 1);
		}
		entityManager.persist(target);
		return "redirect:/admin";
	}
	@RequestMapping(value = "edit", method = RequestMethod.POST, params = "edit")
	@Transactional
	public String edit( Model model, @RequestParam long id, @ModelAttribute User user) {
		User target = entityManager.find(User.class, id);
		if (user.getId() == target.getId()){

			User editado = entityManager.merge(user);
			entityManager.persist(editado);
		}
		return "redirect:/admin";
	}
}