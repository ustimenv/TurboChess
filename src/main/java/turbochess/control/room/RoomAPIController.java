package turbochess.control.room;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import turbochess.control.JsonConverter;
import turbochess.model.User;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;
import turbochess.service.bet.BetException;
import turbochess.service.bet.BetService;
import turbochess.service.participant.ParticipantException;
import turbochess.service.participant.ParticipantService;
import turbochess.service.room.RoomException;
import turbochess.service.room.RoomService;
import turbochess.service.user.UserException;
import turbochess.service.user.UserService;

import javax.persistence.EntityManager;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static java.text.MessageFormat.format;

@RestController
@RequestMapping("/api/room")
public class RoomAPIController{
    private static Logger log = LogManager.getLogger(RoomAPIController.class);

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private UserService userService;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private BetService betService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private EntityManager entityManager;


    /**
     *  @param args: capacity : int (>=2)
     * */
    @PostMapping(value="/create", produces = "application/json")
    @Transactional
    public Map<String, String> createRoom(@RequestHeader Map<String, String> headers,
                                          @RequestBody Map<String, Integer> args, Principal principal) throws Exception{
        headers.forEach((key, value) -> {
            log.info(String.format("Header '%s' = %s", key, value));
        });


        User requestSender = userService.getUserByUsername(principal.getName());
        Room roomCreated = roomService.createRoom(generateRoomCode(), args.getOrDefault("capacity", 2));
        Participant p = participantService.createParticipant(roomCreated, requestSender);
        p.setRole(roomCreated.assignRole(p));
        p.setColour(Participant.Colour.WHITE);
        roomCreated.addParticipant(p);
        entityManager.persist(p);
        entityManager.persist(roomCreated);

        return Map.of("room_code_assigned", roomCreated.getCode(),
                      "colour_assigned",   p.getColourString());

    }

    /**
     *  @param args: room_code : String
     * */
    @PostMapping(value = "/join", produces = "application/json")
    @Transactional
    public Map<String, String> joinRoom(@RequestBody Map<String, String> args, Principal principal) throws ParticipantException, RoomException, BetException, UserException{

        User requestSender = userService.getUserByUsername(principal.getName());
        Room roomToJoin = roomService.getRoomByCode(args.get("room_code"));

        if(participantService.isUserInRoom(roomToJoin, requestSender)){
            return retrieveRoomStateForUser(roomToJoin, requestSender);
        }

        if(!roomToJoin.isBelowCapacity())	throw new RoomException(format("Capacity exceeded for room {0)", roomToJoin.getCode()));

        Participant p = participantService.createParticipant(roomToJoin, requestSender);
        p.setRole(roomToJoin.assignRole(p));

        if(p.getRole() == Participant.Role.OBSERVER){
            p.setColour(Participant.Colour.NONE);
        } else{
            p.setColour(Participant.Colour.BLACK);
        }
        roomToJoin.addParticipant(p);
        entityManager.persist(p);
        entityManager.persist(roomToJoin);
        return Map.of("colour_assigned", p.getColourString(),
                      "fen",            "",
                      "accumulated_bet", String.valueOf(0));
    }


    private Map<String, String> retrieveRoomStateForUser(Room room, User user) throws ParticipantException, BetException{
        Participant p = participantService.getParticipantByUsernameAndRoom(room, user);
        int participantAccumulatedBet = betService.getParticipantTotalBet(p);

        return Map.of("colour_assigned", p.getColourString(),
                      "fen",             room.getFen(),
                      "accumulated_bet", String.valueOf(participantAccumulatedBet));
    }

    /**
     *  @param args: room_code : String
     * */
    @PostMapping(value = "/game_over", produces = "application/json")
    @Transactional
    public Map<String, String> endGame(@RequestBody Map<String, String> args,Principal principal) throws RoomException{
//            LocalDateTime currentTime = LocalDateTime.now();
//            User requestSender = getUserByUsername(principal.getName());
//            Room room = roomService.getRoomByCode(roomCode);
//            Participant.Colour senderColour = participantService.getParticipantByUsernameAndRoom(room, userFrom).getColour();
//
//            long whitesId = participantService.getUserIdsInRoomWithRole(room.getCode(), Participant.Role.PLAYER1).get(0);
//            long blacksId = participantService.getUserIdsInRoomWithRole(room.getCode(), Participant.Role.PLAYER2).get(0);
//            User whites = getUserByID(whitesId);
//            User blacks = getUserByID(blacksId);
//
//            Game.Result result;
//            if("WIN".equals(packet.getResult()) && senderColour == Participant.Colour.WHITE ||
//              "LOSS".equals(packet.getResult()) && senderColour == Participant.Colour.BLACK){
//                result = Game.Result.WHITES_WON;
//            } else if("WIN".equals(packet.getResult()) && senderColour == Participant.Colour.BLACK ||
//                     "LOSS".equals(packet.getResult()) && senderColour == Participant.Colour.WHITE){
//                result = Game.Result.BLACKS_WON;
//            } else{
//                result = Game.Result.DRAW;
//            }
//
//            Game game = new Game(whites, blacks, currentTime, result, room.getMoves());
//
//            List<Bet> winningBets = betService.getRoomBetsByResult(room.getCode(), result);
//            for(Bet B : winningBets){
//                User u = B.getBetter().getUser();
//                setUserCoins(u.getId(), u.getCoins()+Bet.returnOnBet(B.getValue(), B.getTurnPlaced(), room.getCurrentTurn()/2));
//                entityManager.remove(B);
//            }
//
//            entityManager.persist(game);
//
////            List <Participant> participants = participantService.getRoomParticipants(room);
////            for(Participant p : participants){
////                entityManager.remove(p);
////            }
//            participantService.deleteRoomParticipants(room.getCode());
//            entityManager.remove(room);
//            //betService.deleteRoomBets(room);
//            log.info(format("Game saved for users {0} and {1} successfully", whites.getUsername(), blacks.getUsername()));

            Map<String, String> response = new HashMap<>();
            response.put("okay", "okay");
            return response;

    }

    @GetMapping(value = "/list_available", produces = "application/json")
    public Map<String, String> listRooms(Principal principal) throws JsonProcessingException{
        Map<String, String> response = new HashMap<>();
        List<Room> rooms = roomService.getAvailableRooms(10);
        if(rooms.isEmpty())  return null;

        String jsonString = JsonConverter.INSTANCE.toJSONString(rooms);
        response.put("rooms", jsonString);
        return response;
    }

    @GetMapping("/rooms")
    public String getRooms(Model model) {
        List<Room> rooms = roomService.getAvailableRooms(10);
        model.addAttribute("rooms", rooms);
        return "rooms";
    }

    private char[] letters = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ"+"ABCDEFGHIJKLMNOPQRSTUVWXYZ".toLowerCase()).toCharArray();
    private int currentCodeLength=3;
    private long numAttemptsUntilCodeLengthIncrement=2*factorial(currentCodeLength);

    private static long factorial(int number){
        long i, fact=1;
        for(i=1;i<=number;i++){
            fact=fact*i;
        }
        return number;
    }

    private static long permutations(int r, int n){
        return factorial(n) / factorial(n-r);
    }

    // TODO code length trimming
    protected String generateRoomCode(){
        StringBuilder sb = new StringBuilder();
        String generatedCode;
        int currentAttempt = 0;
        while(true){
            // In general, there are nPr ways to arrange n characters in a string of length r.
            // Even though  trying 2n! times doesnt guarantee every one of the possible code will be tried,
            // it provides a sufficient approximation at a sufficiently low cost.
            numAttemptsUntilCodeLengthIncrement=2*permutations(currentCodeLength, letters.length);
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
        return generatedCode;
    }
}