package turbochess.control.room;

//import jdk.vm.ci.meta.Local;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import turbochess.control.JsonConverter;
import turbochess.model.User;
import turbochess.model.chess.Bet;
import turbochess.model.messaging.client.*;
import turbochess.model.messaging.server.*;
import turbochess.model.chess.Game;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;
import turbochess.repository.RoomRepository;
import turbochess.service.bet.BetException;
import turbochess.service.participant.ParticipantException;
import turbochess.service.room.RoomException;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.text.MessageFormat.format;

@Controller
public class RoomAPIController extends RoomController{
    private static Logger log = LogManager.getLogger(RoomAPIController.class);
    @Autowired
    protected RoomRepository roomRepository;
    @Autowired
    protected EntityManager entityManager;

    @RequestMapping(value = "/api/create_room", method=RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Transactional
    public Map<String, String> createRoom(@RequestBody CreateRoomPacket packet) throws ParticipantException{
        log.info(format("[create room]: received packet{0}", packet));

        Room createdRoom = null;
        User userFrom = null;
        try{
            userFrom = getUserByUsername(packet.getFrom());
            // code generation guarantees room code is unique, if implementation changes will have to check for room's existence
            createdRoom = roomService.createRoom(nextRoomCode(), Integer.parseInt(packet.getCapacity()));
            Participant p = participantService.createParticipant(createdRoom, userFrom);
            p.setRole(createdRoom.assignRole(p));
            p.setColour(Participant.Colour.WHITE);
            createdRoom.addParticipant(p);
            entityManager.persist(p);
            entityManager.persist(createdRoom);
            log.info(format("Room {0} created successfully by {1}", createdRoom.getCode(), p.getUser().getUsername()));

            return (Map<String, String>) Stream.of("roomCodeAssigned", createdRoom.getCode(),
                          "colourAssigned", p.getColourString()
                         );

        } catch(RoomException e){
            log.error(format("[create room]: failed to create room {0}", (Object) e.getMessage()));
            return null;		                    // TODO change to smth like 505?
        } catch(ParticipantException e){
            return retrieveRoomStateForUser(createdRoom, userFrom);
        }
    }

    @RequestMapping(value = "/api/join_room", method=RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Transactional
    public Map<String, String> joinRoom(@RequestBody JoinRoomPacket packet) throws ParticipantException{
        log.info(format("[join room]: received packet{0}", packet));
        User userFrom = null;
        Room room = null;
        try{
            userFrom = getUserByUsername(packet.getFrom());
            room = roomService.getRoomByCode(packet.getRoomToJoin());
            if(participantService.isUserInRoom(room, userFrom)){
                return retrieveRoomStateForUser(room, userFrom);
            }

            if(!room.isBelowCapacity())	throw new RoomException(format("Capacity exceeded for room {0)", room.getCode()));

            Participant p = participantService.createParticipant(room, userFrom);
            p.setRole(room.assignRole(p));

            if(p.getRole() == Participant.Role.OBSERVER){
                p.setColour(Participant.Colour.NONE);
            } else{
                p.setColour(Participant.Colour.BLACK);
            }
            room.addParticipant(p);
            entityManager.persist(p);
            entityManager.persist(room);
            log.info(format("User {0} joined room {1} successfully", userFrom.getUsername(), room.getCode()));
            return (Map<String, String>) Stream.of("colourAssigned", p.getColourString(),
                          "fen",            "",
                          "accumulatedBet", String.valueOf(0)
                         );

        } catch(RoomException e){
            log.error(format("[join room]: User failed to join room. Packet:\n {0}\n" +
                                                                    "Error:\n{1}", packet, e.getMessage()));
            return null;
        } catch(ParticipantException e){
            return retrieveRoomStateForUser(room, userFrom);
        }
    }

    private Map<String, String> retrieveRoomStateForUser(Room room, User user) throws ParticipantException{
        // the only way we end here is if user is already present in the room, so disregard the exception
        log.info(format("[get room]: Retrieving room state for {0} for user {1}", room.getCode(), user.getUsername()));
        Participant p = participantService.getParticipantByUsernameAndRoom(room, user);
        int participantAccumulatedBet =0;
        try{
            participantAccumulatedBet = betService.getParticipantTotalBet(p);
        } catch(BetException e){
            log.info(e);
        }
        return (Map<String, String>) Stream.of("colourAssigned", p.getColourString(),
                      "fen",            room.getFen(),
                      "accumulatedBet", String.valueOf(participantAccumulatedBet)
                     );
    }

    @RequestMapping(value = "/api/game_over", method=RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Transactional
    public Map<String, String> endGame(@RequestBody GameOverPacket packet) throws RoomException{
        log.info(format("[end game]: received packet{0}", packet));
        LocalDateTime currentTime = LocalDateTime.now();
        try{
            User userFrom = getUserByUsername(packet.getFrom());
            Room room = roomService.getRoomByCode(packet.getContext());
            Participant.Colour senderColour = participantService.getParticipantByUsernameAndRoom(room, userFrom).getColour();

            long whitesId = participantService.getUserIdsInRoomWithRole(room.getCode(), Participant.Role.PLAYER1).get(0);
            long blacksId = participantService.getUserIdsInRoomWithRole(room.getCode(), Participant.Role.PLAYER2).get(0);
            User whites = getUserByID(whitesId);
            User blacks = getUserByID(blacksId);

            Game.Result result;
            if("WIN".equals(packet.getResult()) && senderColour == Participant.Colour.WHITE ||
              "LOSS".equals(packet.getResult()) && senderColour == Participant.Colour.BLACK){
                result = Game.Result.WHITES_WON;
            } else if("WIN".equals(packet.getResult()) && senderColour == Participant.Colour.BLACK ||
                     "LOSS".equals(packet.getResult()) && senderColour == Participant.Colour.WHITE){
                result = Game.Result.BLACKS_WON;
            } else{
                result = Game.Result.DRAW;
            }

            Game game = new Game(whites, blacks, currentTime, result, room.getMoves());

            List<Bet> winningBets = betService.getRoomBetsByResult(room.getCode(), result);
            for(Bet B : winningBets){
                User u = B.getBetter().getUser();
                setUserCoins(u.getId(), u.getCoins()+Bet.returnOnBet(B.getValue(), B.getTurnPlaced(), room.getCurrentTurn()/2));
            }

            entityManager.persist(game);
            List <Participant> participants = participantService.getRoomParticipants(room);
            for(Participant p : participants){
                entityManager.remove(p);
            }
            entityManager.remove(room);
            log.info(format("Game saved for users {0} and {1} successfully", whites.getUsername(), blacks.getUsername()));
            return (Map<String, String>) Stream.of("okay", "okay");
        } catch(RoomException | ParticipantException e){
            log.error(format("[save room]: Failed to save room {0} \n {1}", packet, e.getMessage()));
            return null;
        }
    }

    @RequestMapping(value = "/api/list_rooms", method=RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Map<String, String> listRooms(){
        try{
            Map<String, String> response = new HashMap<>();
            List<Room> rooms = roomService.getAllRooms();//AvailableRooms(10);

            if(rooms.isEmpty())  return null;

            String jsonString = JsonConverter.INSTANCE.toJSONString(rooms);

            for(int i=0; i<20; i++){
//                System.out.println(rooms.get(0));
                System.out.println("________________________________________________");
                System.out.println(jsonString);
            }
            System.out.println(jsonString);
            response.put("rooms", jsonString);

            log.info("List of rooms retrieved successfully");
            return response;

        } catch(JsonProcessingException e){
            return null;
        }
    }

    @GetMapping("/rooms")
    public String getRooms(Model model) {
        List<Room> rooms = (List<Room>) roomRepository.findAll();

        model.addAttribute("rooms", rooms);

        return "rooms";
    }
}
