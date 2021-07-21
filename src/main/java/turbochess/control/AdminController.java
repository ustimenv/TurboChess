package turbochess.control;
import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import turbochess.model.AdminMessage;
import turbochess.service.admin_message.AdminMessageService;
import turbochess.service.friendship.FriendshipService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import turbochess.LocalData;
import turbochess.model.User;
import turbochess.service.user.UserException;
import turbochess.service.user.UserService;

/**
 * Admin-only controller
 * @author mfreire
 */
@Controller()
@RequestMapping("admin")
public class AdminController {
	
	 private static final Logger log = LogManager.getLogger(AdminController.class);

    @Autowired
    private FriendshipService friendshipservice;

	@Autowired
	private AdminMessageService adminMessageService;

	@Autowired
	private UserService userService;

	 @Autowired
	 private LocalData localData;
	
	 @Autowired
	 private Environment env;
	
	 @GetMapping("")
	 public String index(Model model) {
		List<User> users = userService.getAllUsers();
		List<AdminMessage> messages = adminMessageService.getAll();
		model.addAttribute("usersList", users);	// TODO delete one of these
		model.addAttribute("users",users);

		model.addAttribute("messages", messages);
		model.addAttribute("activeProfiles", env.getActiveProfiles());
		model.addAttribute("basePath", env.getProperty("es.ucm.fdi.base-path"));
		model.addAttribute("debug", env.getProperty("es.ucm.fdi.debug"));

	 	return "admin";
	 }

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "toggleuser")
	@Transactional
	public String toggleuser(Model model, @RequestParam long id, User user) throws UserException{
		User target = userService.getUserById(id);

		if (target.getEnabled() == 1) {
			// remove profile photo
			File f = localData.getFile("user", ""+id);
			if (f.exists()) {
				f.delete();
			}
			// disable user
			target.setEnabled((byte) 0);
		} else {
			// enable user
			target.setEnabled((byte) 1);
		}
		userService.save(target);
		return "redirect:/admin";
	}
	@RequestMapping(value = "edit", method = RequestMethod.POST, params = "edit")
	@Transactional
	public String edit( Model model, @RequestParam long id, @ModelAttribute User user) throws UserException{
		User target = userService.getUserById(id);
		if (user.getId() == target.getId()){
			userService.save(user);
		}
		return "redirect:/admin";
	}
}