package es.ucm.fdi.iw.turbochess.control;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import es.ucm.fdi.iw.turbochess.GeneralUtils;
import es.ucm.fdi.iw.turbochess.model.User;

@Controller
public class RootController {

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
        return "login.html";
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
        return "profile";
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
        model.addAttribute("title", "Turbochess Sing Up");
        return "register";
    }
}
