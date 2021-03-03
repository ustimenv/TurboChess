package com.example.iw.control;

import java.util.Random;

import javax.servlet.http.HttpSession;

import com.example.iw.Guess;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class RootController {

    private static Logger log = LogManager.getLogger(
        RootController.class);

    private static final String OBJETIVO = "o";
    private static final String INTENTOS = "i";
    private static final String RESULTADO = "resultado";
    private final Random random = new Random();
    
    @GetMapping("/")            
    public String index(
    		HttpSession session,
            Model model,
            @RequestParam(required = false) Integer entero) {
                return "foo";
    }

    @RequestMapping(value = "/guess", method = RequestMethod.GET)
    public String foo(Model model) {
        Guess guess = new Guess();
        model.addAttribute("guess", guess);
        return "??!!";

    }
    
}