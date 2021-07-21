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
import turbochess.model.room.Bet;
import turbochess.model.room.Game;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;
import turbochess.service.bet.BetException;
import turbochess.service.bet.BetService;
import turbochess.service.game.GameService;
import turbochess.service.participant.ParticipantException;
import turbochess.service.participant.ParticipantService;
import turbochess.service.room.RoomException;
import turbochess.service.room.RoomService;
import turbochess.service.user.UserException;
import turbochess.service.user.UserService;

import java.security.Principal;
import java.time.LocalDateTime;
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
    private GameService gameService;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private BetService betService;

    @Autowired
    private RoomService roomService;

    /**
     *  @param args: capacity : int (>=2)
     * */
    @PostMapping(value="/create", produces = "application/json")
    @Transactional
    public Map<String, String> createRoom(@RequestHeader Map<String, String> headers,
                                          @RequestBody Map<String, Integer> args, Principal principal) throws Exception{

        User requestSender = userService.getUserByUsername(principal.getName());
        Room roomCreated = roomService.createRoom(generateRoomCode(), args.getOrDefault("capacity", 2));
        Participant p = participantService.createParticipant(roomCreated, requestSender);
        p.setRole(roomCreated.assignRole(p));
        p.setColour(Participant.Colour.WHITE);
        roomCreated.addParticipant(p);
        roomService.save(roomCreated);
        participantService.save(p);

        return Map.of("room_code_assigned", roomCreated.getCode(),
                      "colour_assigned",   p.getColourString(),
                      "num_participants", String.valueOf(roomCreated.getNumParticipants()));

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
            Participant p = participantService.getParticipantByUsernameAndRoom(roomToJoin, requestSender);
            int participantAccumulatedBet = betService.getParticipantTotalBet(p);

            return Map.of("colour_assigned", p.getColourString(),
                          "fen",             roomToJoin.getFen(),
                          "accumulated_bet", String.valueOf(participantAccumulatedBet),
                          "num_participants", String.valueOf(roomToJoin.getNumParticipants()));
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
        participantService.save(p);
        roomService.save(roomToJoin);
        return Map.of("colour_assigned", p.getColourString(),
                      "fen",            "",
                      "accumulated_bet", String.valueOf(0),
                      "num_participants", String.valueOf(roomToJoin.getNumParticipants()));
    }

    /**
     *  @param args: room_code : String
     * */
    @PostMapping(value = "/draw", produces = "application/json")
    @Transactional
    public Map<String, String> draw(@RequestBody Map<String, String> args, Principal principal) throws RoomException, UserException, ParticipantException{
        LocalDateTime currentTime = LocalDateTime.now();
        Room room = roomService.getRoomByCode(args.get("room_code"));
        Participant p = participantService.getParticipantByUsernameAndRoom(room, userService.getUserByUsername(principal.getName()));
        Participant opponent;

        if(p.getRole() == Participant.Role.PLAYER1 || p.getRole() == Participant.Role.PLAYER2){
            User whites = userService.getUserById(participantService.getUserIdsInRoomWithRole(room.getCode(), Participant.Role.PLAYER1).get(0));
            User blacks = userService.getUserById(participantService.getUserIdsInRoomWithRole(room.getCode(), Participant.Role.PLAYER2).get(0));
            if(p.getUser().getId() == whites.getId()){
                opponent = participantService.getParticipantByUsernameAndRoom(room, blacks);
            } else  opponent = participantService.getParticipantByUsernameAndRoom(room, whites);

            if(room.getDrawProposer() == null){                     // first player to offer draw
                room.setDrawProposer(p.getUser().getId());
                roomService.save(room);
            } else if(room.getDrawProposer() == opponent.getUser().getId()){    // p accepting draw proposed by opponent
                p.getUser().updateScoreOnDraw(opponent.getUser());
                opponent.getUser().updateScoreOnDraw(p.getUser());

            }
            userService.save(p.getUser());
            userService.save(opponent.getUser());
            gameService.save(new Game(whites, blacks, currentTime, Game.Result.DRAW, room.getMoves()));
            log.info(format("Game saved for users {0} and {1} successfully", whites.getUsername(), blacks.getUsername()));
        }
        return Map.of("okay", "okay");
    }

    @PostMapping(value = "/victory", produces = "application/json")
    @Transactional
    public Map<String, String> winGame(@RequestBody Map<String, String> args, Principal principal) throws RoomException, UserException, ParticipantException{
        LocalDateTime currentTime = LocalDateTime.now();
        Room room = roomService.getRoomByCode(args.get("room_code"));
        Participant winner = participantService.getParticipantByUsernameAndRoom(room, userService.getUserByUsername(principal.getName()));
        if(winner.getRole() == Participant.Role.PLAYER1 || winner.getRole() == Participant.Role.PLAYER2){

            User whites = userService.getUserById(participantService.getUserIdsInRoomWithRole(room.getCode(), Participant.Role.PLAYER1).get(0));
            User blacks = userService.getUserById(participantService.getUserIdsInRoomWithRole(room.getCode(), Participant.Role.PLAYER2).get(0));

            Participant loser;
            Game.Result gameResult;
            if(winner.getUser().getId() == whites.getId()){
                gameResult = Game.Result.WHITES_WON;
                loser = participantService.getParticipantByUsernameAndRoom(room, blacks);
            } else{
                gameResult = Game.Result.BLACKS_WON;
                loser = participantService.getParticipantByUsernameAndRoom(room, whites);
            }
            // in order to declare victory, the 'loser' must be inactive, having left the match

            if(loser.getLastActiveTime() == null){
                throw new UserException(format("User {0} is attempting to illegally claim victory over {1}",
                        winner.getUser().getUsername(), loser.getUser().getUsername()));
            }
            winner.getUser().updateScoreOnVictory(loser.getUser());
            loser.getUser().updateScoreOnVictory(winner.getUser());
            userService.save(winner.getUser());
            userService.save(loser.getUser());
            gameService.save(new Game(whites, blacks, currentTime, gameResult, room.getMoves()));

            closeRoom(room, gameResult);
        }
        return Map.of("okay", "okay");
    }

    @PostMapping(value = "/loss", produces = "application/json")
    @Transactional
    public Map<String, String> loseGame(@RequestBody Map<String, String> args, Principal principal) throws RoomException, UserException, ParticipantException{
        LocalDateTime currentTime = LocalDateTime.now();

        Room room = roomService.getRoomByCode(args.get("room_code"));
        Participant loser = participantService.getParticipantByUsernameAndRoom(room, userService.getUserByUsername(principal.getName()));
        if(loser.getRole() == Participant.Role.PLAYER1 || loser.getRole() == Participant.Role.PLAYER2){
            User whites = userService.getUserById(participantService.getUserIdsInRoomWithRole(room.getCode(), Participant.Role.PLAYER1).get(0));
            User blacks = userService.getUserById(participantService.getUserIdsInRoomWithRole(room.getCode(), Participant.Role.PLAYER2).get(0));
            Participant winner;
            Game.Result gameResult;

            if(loser.getUser().getId() == whites.getId()){
                gameResult = Game.Result.BLACKS_WON;
                winner = participantService.getParticipantByUsernameAndRoom(room, blacks);
            } else{
                gameResult = Game.Result.WHITES_WON;
                winner = participantService.getParticipantByUsernameAndRoom(room, whites);
            }

            winner.getUser().updateScoreOnVictory(loser.getUser());
            loser.getUser().updateScoreOnVictory(winner.getUser());
            userService.save(winner.getUser());
            userService.save(loser.getUser());
            gameService.save(new Game(whites, blacks, currentTime, gameResult, room.getMoves()));
            closeRoom(room, gameResult);
        }
        return Map.of("okay", "okay");
    }

//    @Transactional
    private void closeRoom(Room room, Game.Result gameResult){
        List<Bet> winningBets = betService.getRoomBetsByResult(room.getCode(), gameResult);
        for(Bet B : winningBets){
            User u = B.getBetter().getUser();
            u.setCoins(u.getCoins() + Bet.returnOnBet(B.getAmount(), B.getTurnPlaced(), room.getCurrentTurn()/2));
            userService.save(u);
            betService.delete(B);
        }
        participantService.getRoomParticipants(room).forEach(participant -> participantService.delete(participant));
        roomService.delete(room);
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