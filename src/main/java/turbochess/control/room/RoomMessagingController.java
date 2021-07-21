package turbochess.control.room;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import turbochess.model.room.Bet;
import turbochess.model.room.Move;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;
import turbochess.service.bet.BetService;
import turbochess.service.participant.ParticipantException;
import turbochess.service.participant.ParticipantService;
import turbochess.service.room.RoomException;
import turbochess.service.room.RoomService;
import turbochess.service.user.UserException;
import turbochess.service.user.UserService;

import java.security.Principal;
import java.util.Map;
import java.util.Objects;

import static java.text.MessageFormat.format;

@Controller
public class RoomMessagingController{
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

    private static Logger log = LogManager.getLogger(RoomMessagingController.class);

    @MessageMapping("/{room}.chat.sendMessage")
    public void sendMessage(@DestinationVariable String room, Map<String, String> message, SimpMessageHeaderAccessor accessor) throws ParticipantException{
        Participant p = getParticipantByRoomCodeAndPrincipal(room, accessor.getUser());
        SimpMessageHeaderAccessor responseHeader = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        responseHeader.setHeader("FROM", Objects.requireNonNull(accessor.getUser()).getName());
        responseHeader.setHeader("TYPE", "TEXT_MESSAGE");
        template.convertAndSend("/queue/"+room, message.get("text"), responseHeader.toMap());
    }

    @MessageMapping("/{room}.sys.sendMove")
    @Transactional
    public void sendMove(@DestinationVariable String room, Move move, SimpMessageHeaderAccessor accessor) throws ParticipantException{
        Participant p = getParticipantByRoomCodeAndPrincipal(room, accessor.getUser());

        SimpMessageHeaderAccessor responseHeader = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        responseHeader.setUser(accessor.getUser());
        responseHeader.setHeader("TYPE", "CHESS_MOVE");
        responseHeader.setHeader("FROM", Objects.requireNonNull(accessor.getUser()).getName());


        boolean isValidWhites = (p.getColour() == Participant.Colour.WHITE &&
                                (p.getRoom().getGameState() == Room.GameState.WHITES_TURN) ||
                                        (p.getRoom().getGameState() == Room.GameState.NOT_STARTED));

        boolean isValidBlacks = (p.getColour() == Participant.Colour.BLACK &&
                                 p.getRoom().getGameState() == Room.GameState.BLACKS_TURN);

        if(isValidWhites){
            p.getRoom().setGameState(Room.GameState.BLACKS_TURN);
        } else if(isValidBlacks){
            p.getRoom().setGameState(Room.GameState.WHITES_TURN);
        } else{
            throw new ParticipantException(format("Participant {0} can't move for {1} in room {2} at this time",
                                        p.getUser().getUsername(), p.getRoom().getGameState(), p.getRoom().getCode()));
        }
        p.getRoom().setCurrentTurn(p.getRoom().getCurrentTurn()+1);

        String fen = move.getFen();
        if(fen != null && !fen.isEmpty())    p.getRoom().setFen(fen);
        p.getRoom().setMoves(p.getRoom().getMoves() + Move.DELIM + move);
        roomService.save(p.getRoom());
        template.convertAndSend("/queue/"+room, move, responseHeader.toMap());
    }

    @MessageMapping("/{room}.sys.placeBet")
    @Transactional
    public void placeBet(@DestinationVariable String room, @Payload Bet bet, SimpMessageHeaderAccessor accessor) throws ParticipantException, UserException{
        Participant p = getParticipantByRoomCodeAndPrincipal(room, accessor.getUser());

        SimpMessageHeaderAccessor responseHeader = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        responseHeader.setUser(accessor.getUser());
        responseHeader.setHeader("FROM", Objects.requireNonNull(accessor.getUser()).getName());
        responseHeader.setHeader("TYPE", "BET_PLACED");

        bet.setTurnPlaced(p.getRoom().getCurrentTurn());
        bet.setBetter(p);

        int userBalance = p.getUser().getCoins();
        int betAmount = bet.getAmount();

        if(betAmount <= 0){
            throw new NumberFormatException("Invalid bet value: " + betAmount);
        }
        if(userBalance < betAmount){
            throw new UserException(format("Insufficient ({0}) balance ({1}) for player {2}",
                                                    userBalance, betAmount, p.getUser().getUsername()));
        } else   p.getUser().setCoins(userBalance - betAmount);
        userService.save(p.getUser());
        betService.save(bet);
        template.convertAndSend("/queue/"+room,
                                format("{0} has increased their bet by {1}", p.getUser().getUsername(), bet.getAmount()),
                                responseHeader.toMap());

    }

    private Participant getParticipantByRoomCodeAndPrincipal(String roomCode, Principal principal) throws ParticipantException{
        try{
            return participantService.getParticipantByUsernameAndRoom(roomService
                    .getRoomByCode(roomCode), userService.getUserByUsername(principal.getName()));
        } catch(RoomException | UserException e){
            throw new ParticipantException(e.getMessage());
        }
    }
}