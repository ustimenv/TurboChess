package turbochess.control.room;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import turbochess.model.Player;
import turbochess.model.User;
import turbochess.model.messaging.MessagePacket;
import turbochess.model.messaging.ResponsePacket;
import turbochess.model.room.Game;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;
import turbochess.service.room.RoomException;

import javax.persistence.Query;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;

@Controller
public class RoomAJAXController extends RoomController{
    private static Logger log = LogManager.getLogger(RoomAJAXController.class);

    @RequestMapping(value = "/api/create_room", method=RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Transactional
    public ResponsePacket createRoom(@RequestBody MessagePacket packet, HttpSession session){

        log.info(format("[create room]: received packet{0}", packet));
        try{
            String from = packet.getFrom();
            String code = nextRoomCode();					// code generation guarantees room code is unique
            User userFrom = getUserByUsername(from);
            Room createdRoom = roomService.createRoom(code, Integer.parseInt(packet.getPayload()));
            Participant p = new Participant(createdRoom, userFrom);
            p.setRole(roomService.assignRole(code, p));
            p.setColour(Participant.Colour.WHITE);
            //se crea el juego con el jugador
            Player p1 = new Player();
            p1.setUser((User) session.getAttribute("u"));
            p1.setWhite(true);
            Game g = new Game();
            g.setPlayers(p1);
            g.setCreationDate(LocalDateTime.now());
            g.setGameType(Game.GameType.NORMAL);
            g.setRoom_code(code);
            roomService.joinRoom(createdRoom.getCode(), p);
            entityManager.persist(p);
            entityManager.persist(p1);
            entityManager.persist(g);
            log.info(format("Room {0} created successfully by {1}", code, p.getUser().getUsername()));
            return new ResponsePacket(p.getColourString(), code);
        } catch(RoomException e){
            log.error(format("[create room]: failed to create room {0}", e.getMessage()));
            return null;		// TODO change to 505 or smth
        }
    }

    @RequestMapping(value = "/api/join_room", method=RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Transactional
    public ResponsePacket joinRoom(@RequestBody MessagePacket packet){
        log.info(format("[join room]: received packet{0}", packet));
        String from=null, code=null;
        try{
            from = packet.getFrom();
            code = packet.getPayload();
            User userFrom = getUserByUsername(from);
            if(!roomService.isRoomBelowCapacity(code))	throw new RoomException(format("Capacity exceeded for room {0)", code));

            Room roomToJoin = roomService.getRoomByCode(code);
            Participant p = new Participant(roomToJoin, userFrom);
            p.setRole(roomService.assignRole(code, p));
            if(p.getRole() == Participant.Role.OBSERVER){
                p.setColour(Participant.Colour.NONE);
            } else{
                Query query = entityManager.createQuery("SELECT g FROM Game g WHERE g.room_code = :room_code")
                        .setParameter("room_code", code);
                Game g = (Game) query.getSingleResult();
                Player p2 = new Player();
                p2.setUser(userFrom);
                p2.setWhite(false);
                g.setPlayers(p2);
                p.setColour(Participant.Colour.BLACK);
                entityManager.persist(p2);
                entityManager.persist(g);
            }
            roomService.joinRoom(code, p);
            entityManager.persist(p);
            log.info(format("User {0} joined room {1} successfully", from, code));
            return new ResponsePacket(p.getColourString(), "oki");
        } catch(RoomException e){
            log.error(format("[join room]: User {0}failed to join room {1} \n{2}", from, code, e.getMessage()));
            return null;
        }
    }

    @RequestMapping(value = "/api/save_room", method=RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Transactional
    public ResponsePacket saveRoom(@RequestBody MessagePacket packet){
        log.info(format("[save room]: received packet{0}", packet));
        try{
            String boardState = packet.getPayload();
            Room contextRoom = roomService.getRoomByCode(packet.getContext());
            User userFrom = getUserByUsername(packet.getFrom());
            Participant.Role role = getParticipantRole(userFrom, contextRoom);
            if(role != Participant.Role.PLAYER1)    // ie must be the owner of the room
                throw new RoomException(format("User {0} doesn't have have the permission to save room {1}",
                                                userFrom.getUsername(), contextRoom.getCode()));

            List<String> participantJSONs=new ArrayList<>();
            for(Participant p :getRoomParticipants(contextRoom)){
                participantJSONs.add(p.toJSON());
            }
//            for(String s : participantJSONs){
//                System.out.println(s);
//            }
            roomService.prepareAndSave(contextRoom.getCode(), boardState,
                                        participantJSONs.stream().map(Object::toString).collect(Collectors.joining(",")));
            log.info(format("Room {0} saved successfully by {1}", contextRoom.getCode(), userFrom.getUsername()));
            return new ResponsePacket("", "oki");
        } catch(RoomException e){
            log.error(format("[save room]: error--> {0}", e.getMessage()));
            return null;
        }
    }



}
