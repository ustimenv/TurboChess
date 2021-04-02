package es.ucm.fdi.iw.turbochess.control;


//import jdk.internal.org.jline.utils.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.iw.turbochess.GeneralUtils;
import es.ucm.fdi.iw.turbochess.model.User;

@Controller
public class RootController {

    @GetMapping("/")
    public String index(Model model) {
        return "index";
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
    @GetMapping("/login")
    public String getUserLoginPage(Model model) {
        model.addAttribute("title", "Turbochess LogIn");
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("title", "Turbochess Sing Up");
        return "register";
    }
}
