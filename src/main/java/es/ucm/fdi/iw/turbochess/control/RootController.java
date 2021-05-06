package es.ucm.fdi.iw.turbochess.control;

import java.util.List;

import antlr.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import es.ucm.fdi.iw.turbochess.GeneralUtils;
import es.ucm.fdi.iw.turbochess.model.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

@Controller
public class RootController {
    @PersistenceContext
   // @Autowired
    private EntityManager entityManager;

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/login")
    public String getUserLoginPage(Model model) {
        model.addAttribute("title", "Turbochess LogIn");
        return "login";
    }

    @RequestMapping("/login-error.html")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login-error";
    }

    @GetMapping("/game")
    public String game(Model model) {
        model.addAttribute("title", "Turbochess");
        return "game";
    }

    @GetMapping("/spect")
    public String spect(Model model) {
        model.addAttribute("title", "Turbochess Spect");
        return "spect";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        return "user";
    }
    
    @GetMapping("/othersProfile")
    public String othersProfile(Model model) {
        return "othersProfile";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        return "admin";
    }

    @GetMapping("/ranks")
    public String ranks(Model model) {
        List <User> rankings = GeneralUtils.JSONtoList("src/main/resources/examples/rankings.json");
        model.addAttribute("rankings", rankings);
        return "ranks";
    }
    
    @GetMapping(path = "/users/{username}")
    public String erothersProfileror(Model model, @PathVariable(value="username", required = true) String username) {
        model.addAttribute("title", "Turbochess Error");
        if(!username.equals("")){
            model.addAttribute("username", username);
        } else{
            System.err.println("Empty username!!");
        } 
        return "othersProfile";
    }

    @GetMapping("/error")
    public String error(Model model) {
        model.addAttribute("title", "Turbochess Error");
        return "error";
    }


    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "signup_form";
    }

    @PostMapping("/signup_form")
    @Transactional
    public String processRegister(User user,Model model) {
        if(user.getPassword().compareTo(user.getPasswordConfirm()) == 0) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setRoles("USER");
            user.setElo(0);
            user.setEnabled((byte) 1);

            Query query = entityManager.createNamedQuery("User.byUsername");
            query.setParameter("username", user.getUsername());
            List result = query.getResultList();
            if (result.isEmpty()) {
                entityManager.persist(user);
                model.addAttribute("name",user.getUsername() );
                return "register_success";
            } else {
                model.addAttribute("msg", user.getUsername()+" already exists");
                return "signup_form";
            }
        }else {
            model.addAttribute("msg", "The password should be the same");
            return "signup_form";
        }

    }
    
    @GetMapping("/chat")
    public String chat(Model model) {
        return "chat";
    }
}
