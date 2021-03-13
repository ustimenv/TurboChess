package es.ucm.fdi.iw.turbochess.control;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/admin")
    public String admin(Model model) {
        return "admin";
    }

    @GetMapping("/ranks")
    public String ranks(Model model) {
        return "ranks";
    }
}
