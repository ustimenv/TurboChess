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
import turbochess.model.chess.Game;
import turbochess.model.messaging.CreateRoomPacket;
import turbochess.model.messaging.GameOverPacket;
import turbochess.model.messaging.JoinRoomPacket;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;
import turbochess.repository.RoomRepository;
import turbochess.service.bet.BetException;
import turbochess.service.participant.ParticipantException;
import turbochess.service.room.RoomException;

import javax.persistence.EntityManager;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.text.MessageFormat.format;

@Controller
public class RoomAPIController extends RoomController{
    private static Logger log = LogManager.getLogger(RoomAPIController.class);
    @Autowired
    protected RoomRepository roomRepository;
    @Autowired
    protected EntityManager entityManager;

    private char[] letters = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ"+"ABCDEFGHIJKLMNOPQRSTUVWXYZ".toLowerCase())
                                .toCharArray();
    private int currentCodeLength=3;
    private long numAttemptsUntilCodeLengthIncrement=2*factorial(currentCodeLength);
    private static long factorial(int number){
        long i, fact=1;
        for(i=1;i<=number;i++){
            fact=fact*i;
        }
        return number;
    }
    //https://www.google.com/search?q=permutation+formula&oq=permu&aqs=chrome.2.69i57j0i67l5j0l4.2487j0j7&sourceid=chrome&ie=UTF-8
    private static long permutations(int r, int n){
        return factorial(n) / factorial(n-r);
    }

    @RequestMapping(value = "/api/create_room", method=RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Transactional
    public Map<String, String> createRoom(@RequestBody CreateRoomPacket packet, Principal principal) throws ParticipantException{
        log.info(format("[create room]: received packet{0}", packet));
        StringBuilder sb = new StringBuilder();
        String generatedCode=null;
        int currentAttempt = 0;
        while(true){
            // In general, there are nPr ways to arrange n characters in a string of length r.
            // Even though  trying 2n! times doesnt guarantee every one of the possible code will be tried,
            // it provides a sufficient approximation at a sufficiently low cost.
            numAttemptsUntilCodeLengthIncrement=2*permutations(currentCodeLength, letters.length);
            // generate a pseudo random code
            for(int i=0; i<currentCodeLength; i++){
                sb.append(letters[ThreadLocalRandom.current().nextInt(letters.length)]);
            }
            if(!roomService.roomExists(sb.toString())){
                generatedCode = sb.toString();
                break;
            } else  currentAttempt++;
            if(currentAttempt>=numAttemptsUntilCodeLengthIncrement){
                currentCodeLength++;
                currentAttempt=0;
            }
        }
        System.out.println("Code generated: " + generatedCode);
        Room createdRoom = null;
        User userFrom = null;
        try{
            userFrom = getUserByUsername(principal.getName());
            // code generation guarantees room code is unique, if implementation changes will have to check for room's existence
            createdRoom = roomService.createRoom(generatedCode, Integer.parseInt(packet.getCapacity()));
            Participant p = participantService.createParticipant(createdRoom, userFrom);
            p.setRole(createdRoom.assignRole(p));
            p.setColour(Participant.Colour.WHITE);
            createdRoom.addParticipant(p);
            entityManager.persist(p);
            entityManager.persist(createdRoom);
            log.info(format("Room {0} created successfully by {1}", createdRoom.getCode(), p.getUser().getUsername()));

            Map <String, String> response = new HashMap<>();
            response.put("roomCodeAssigned", createdRoom.getCode());
            response.put("colourAssigned", p.getColourString());
            return response;

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
            Map<String, String> response = new HashMap<>();
            response.put("colourAssigned", p.getColourString());
            response.put("fen",            "");
            response.put("accumulatedBet", String.valueOf(0));
            return response;

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
        Map<String, String> response = new HashMap<>();
        response.put("colourAssigned", p.getColourString());
        response.put("fen",            room.getFen());
        response.put("accumulatedBet", String.valueOf(participantAccumulatedBet));
        return response;
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
                entityManager.remove(B);
            }

            entityManager.persist(game);

//            List <Participant> participants = participantService.getRoomParticipants(room);
//            for(Participant p : participants){
//                entityManager.remove(p);
//            }
            participantService.deleteRoomParticipants(room.getCode());
            entityManager.remove(room);
            //betService.deleteRoomBets(room);
            log.info(format("Game saved for users {0} and {1} successfully", whites.getUsername(), blacks.getUsername()));

            Map<String, String> response = new HashMap<>();
            response.put("okay", "okay");
            return response;
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
            List<Room> rooms = roomService.getAvailableRooms(10);

            if(rooms.isEmpty())  return null;

            String jsonString = JsonConverter.INSTANCE.toJSONString(rooms);
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
