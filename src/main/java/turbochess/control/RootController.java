package turbochess.control;

import turbochess.configurations.IwUserDetailsService;
import turbochess.model.Friendship;
import turbochess.model.User;
import turbochess.service.friendship.FriendshipException;
import turbochess.service.friendship.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.List;

@Controller
public class RootController {
    @Autowired
    PasswordEncoder passwordEncoder;
    @PersistenceContext
    // @Autowired
    private EntityManager entityManager;

    @Autowired
    private IwUserDetailsService service;

    @Autowired
    private HttpSession session;

    @Autowired
    private FriendshipService friendshipservice;


    @GetMapping("/")
    public String index(Model model, HttpSession session) {
       if (session.getAttribute("u") != null) {
            User user = (User) session.getAttribute("u");
            if(user.getRoles().contains("ADMIN")){
                List<User> users = entityManager.createNamedQuery("User.findAll").getResultList();
                model.addAttribute("usersList", users);
            }
            if(user.getRoles().contains("USER")) {
                Query query = entityManager.createNativeQuery("SELECT * FROM Friends " +
                        "LEFT JOIN User on Friends.friend_id =User.id WHERE Friends.SUBJECT_ID= :userid " +
                        "UNION ALL" +
                        " SELECT  * FROM Friends LEFT JOIN User on Friends.SUBJECT_ID=User.id WHERE friend_id= :userid", User.class)
                        .setParameter("userid", user.getId());

                List<User> friends = (List<User>) query.getResultList();
                List<Friendship> friendsRequest = friendshipservice.findByReceiverAndState(user, Friendship.State.OPEN);
                if (!friendsRequest.isEmpty()) model.addAttribute("peticiones", friendsRequest);
                // model.addAttribute("peticiones", friendsRequest);
            /*
            friends.stream().forEach((n) -> {
                System.out.println(n.getUsername());
            });*/
                model.addAttribute("friends", friends);
            }
        }
        return "index";
    }

    @GetMapping("/login")
    public String getUserLoginPage(Model model) {
        model.addAttribute("title", "Turbochess LogIn");
        return "login";
    }

    @RequestMapping("/login-error.html")
    public String loginError(Model model) {
        model.addAttribute("msg", "Invalid username or password.");
        return "login";
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
    public String profile(Model model, HttpSession session) {
        User user = (User) session.getAttribute("u");
        return "redirect:user/" + user.getId();
    }

    @GetMapping("/othersProfile")
    public String othersProfile(Model model) {
        return "othersProfile";
    }


    @GetMapping("/ranks")
    public String ranks(Model model) {
//        List<User> rankings = GeneralUtils.JSONtoList("src/main/resources/examples/rankings.json");
//        model.addAttribute("rankings", rankings);
        return "ranks";
    }

    @GetMapping(path = "/users/{username}")
    public String erothersProfileror(Model model, @PathVariable(value = "username", required = true) String username) {
        model.addAttribute("title", "Turbochess Error");
        if (!username.equals("")) {
            model.addAttribute("username", username);
        } else {
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

    @GetMapping("/logout")
    public String logout(Model model) {
        model.addAttribute("title", "Turbochess");
        return "/";
    }

    @PostMapping("/signup_form")
    @Transactional
    public String processRegister(User user, Model model) {
        if (user.getPassword().compareTo(user.getPasswordConfirm()) == 0) {

            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setRoles("USER");
            user.setElo(0);
            user.setMatches_played(0);
            user.setMatches_played(0);
            user.setEnabled((byte) 1);

            List result = entityManager.createNamedQuery("User.byUsername").setParameter("username", user.getUsername()).getResultList();
            if (result.isEmpty()) {
                entityManager.persist(user);
                model.addAttribute("name", user.getUsername());
                return "register_success";
            } else {
                model.addAttribute("msg", user.getUsername() + " already exists");
                return "signup_form";
            }
        } else {
            model.addAttribute("msg", "The password should be the same");
            return "signup_form";
        }

    }

    @GetMapping("/room")
    public String room(Model model) {
        model.addAttribute("username", ((User) session.getAttribute("u")).getUsername());
        return "room";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(@RequestParam(value = "search", required = false) String username, Model model) {
        List<User> searchResults = (List<User>) entityManager.createNamedQuery("User.search_result", User.class)
                .setParameter("username", "%" + username + "%").getResultList();

        model.addAttribute("search", searchResults);
        return "userlist";
    }

    @PostMapping("/requestFriendship")
    @Transactional
    public String requestFriendship(@RequestParam Long userId, Model model) {
        User sender = (User) session.getAttribute("u");
        User receiver = entityManager.find(User.class, userId);
            try {
                friendshipservice.createFriendshipRequest(sender, receiver);
            } catch (FriendshipException e) {
                return "redirect:/";
            }

         return "redirect:user/" + receiver.getId();
    }

    /**
     * Obtener los objetos del usuario con sesión abierta y de la solicitud de amistad.
     * Invocar al método del servicio que correspondan para aceptar o declinar la solicitud.
     * Si la solicitud es aceptada, redirigir al usuario a la vista de perfil del usuario
     * cuya amistad acaba de aceptar.
     * Si es declinada o se produce algún error, redirigir a la página principal.
     * @param requestId
     * @param action
     * @return
     */
    @PostMapping(path = "/answerFriendshipRequest")
    @Transactional
    public String answerFriendshipRequest(@RequestParam Long requestId, @RequestParam String action, Model model) throws FriendshipException {
        User u=(User) session.getAttribute("u");
        User sender= entityManager.find(User.class, requestId);
        Friendship response = friendshipservice.findRequest(sender,u);

        if(action.equals("Accept") ){
            response.setState(Friendship.State.ACCEPTED);
            friendshipservice.acceptFriendshipRequest(response,u);
            model.addAttribute("accept", true);
        }else if(action.equals("Decline") ) {
            response.setState(Friendship.State.DECLINED);
            friendshipservice.declineFriendshipRequest(response, u);
            model.addAttribute("decline", true);
        }
        return "redirect:/";

    }

    @GetMapping("/editProfile")
    public String editProfile(Model model) {
        model.addAttribute("editing", true);
        return "room";
    }

}