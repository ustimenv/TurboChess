package turbochess.control.room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import turbochess.model.room.Room;
import turbochess.repository.RoomRepository;
import turbochess.service.room.RoomService;

import javax.persistence.EntityManager;
import java.util.List;

@Controller
public class RoomsController {
    @Autowired
    protected SimpMessagingTemplate template;

    @Autowired
    protected RoomRepository roomRepository;

    @Autowired
    protected EntityManager entityManager;
    @GetMapping("/rooms")
    public String getRooms(Model model) {
        List<Room> rooms = (List<Room>) roomRepository.findAll();

        model.addAttribute("rooms", rooms);

        return "rooms";
    }


}
