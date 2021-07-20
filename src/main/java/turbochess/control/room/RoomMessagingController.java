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
import turbochess.model.User;
import turbochess.model.chess.Bet;
import turbochess.model.chess.Move;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;
import turbochess.service.participant.ParticipantException;
import turbochess.service.participant.ParticipantService;
import turbochess.service.room.RoomException;
import turbochess.service.room.RoomService;
import turbochess.service.user.UserService;

import javax.persistence.EntityManager;
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
    private RoomService roomService;

    @Autowired
    private EntityManager entityManager;

    private static Logger log = LogManager.getLogger(RoomMessagingController.class);

    @MessageMapping("/{room}.chat.sendMessage")
    public void sendMessage(@DestinationVariable String room, Map<String, String> message, SimpMessageHeaderAccessor accessor) {
        if(room != null){
            SimpMessageHeaderAccessor responseHeader = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
            responseHeader.setHeader("FROM", Objects.requireNonNull(accessor.getUser()).getName());
            responseHeader.setHeader("TYPE", "TEXT_MESSAGE");
            template.convertAndSend("/queue/"+room, message.get("text"), responseHeader.toMap());
        }
    }

    @MessageMapping("/{room}.chat.addUser")
    public void addUser(@DestinationVariable String room, SimpMessageHeaderAccessor accessor) throws RoomException{
        SimpMessageHeaderAccessor responseHeader = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        responseHeader.setUser(accessor.getUser());
        responseHeader.setHeader("TYPE", "USER_JOINED");
        responseHeader.setHeader("FROM", Objects.requireNonNull(accessor.getUser()).getName());

        Room contextRoom = roomService.getRoomByCode(room);
        responseHeader.setHeader("NUM_PARTICIPANTS", String.valueOf(contextRoom.getNumParticipants()));

        String responseString = accessor.getUser().getName() + " has joined!";
        template.convertAndSend("/queue/"+room, responseString, responseHeader.toMap());
    }

    @MessageMapping("/{room}.sys.sendMove")
    @Transactional
    public void sendMove(@DestinationVariable String room, Move move, SimpMessageHeaderAccessor accessor) throws Exception{
        SimpMessageHeaderAccessor responseHeader = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        responseHeader.setUser(accessor.getUser());
        responseHeader.setHeader("TYPE", "CHESS_MOVE");
        responseHeader.setHeader("FROM", Objects.requireNonNull(accessor.getUser()).getName());

        User u = userService.getUserByUsername(Objects.requireNonNull(accessor.getUser()).getName());
        Room r = roomService.getRoomByCode(room);
        Participant p = participantService.getParticipantByUsernameAndRoom(r, u);

        String fen = move.getFen();
        boolean isValidWhites = (p.getColour() == Participant.Colour.WHITE && (r.getGameState() == Room.GameState.WHITE_TURN)
                                                                           || (r.getGameState() == Room.GameState.NOT_STARTED));
        boolean isValidBlacks = (p.getColour() == Participant.Colour.BLACK && r.getGameState() == Room.GameState.BLACK_TURN);

        if(isValidWhites){
            r.setGameState(Room.GameState.BLACK_TURN);
        } else if(isValidBlacks){
            r.setGameState(Room.GameState.WHITE_TURN);
        } else{
            throw new ParticipantException(format("Participant {0} can't move for {1} in room {2} at this time",
                                                        u.getUsername(), r.getGameState(), r.getCode()));
        }

        r.setCurrentTurn(r.getCurrentTurn()+1);
        if(fen != null && fen.isEmpty())    r.setFen(fen);
        r.setMoves(r.getMoves() + Move.DELIM + move);
        entityManager.persist(r);
        template.convertAndSend("/queue/"+room, move, responseHeader.toMap());
    }

    @MessageMapping("/{room}.sys.placeBet")
    @Transactional
    public void placeBet(@DestinationVariable String room, @Payload Bet bet, SimpMessageHeaderAccessor accessor) throws Exception{
        SimpMessageHeaderAccessor responseHeader = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        responseHeader.setUser(accessor.getUser());
        responseHeader.setHeader("FROM", Objects.requireNonNull(accessor.getUser()).getName());
        responseHeader.setHeader("TYPE", "BET_PLACED");

        User u = userService.getUserByUsername(accessor.getUser().getName());
        Room r = roomService.getRoomByCode(room);
        Participant p = participantService.getParticipantByUsernameAndRoom(r, u);
        bet.setTurnPlaced(r.getCurrentTurn());
        bet.setBetter(p);

        int userBalance = u.getCoins();
        int betAmount = bet.getAmount();

        if(betAmount <= 0){
            throw new NumberFormatException("Invalid bet value: " + betAmount);
        }
        if(userBalance < betAmount){
            throw new RoomException(format("Insufficient ({0}) balance ({1}) for player {2}",userBalance, betAmount, u.getUsername()));
        }else   u.setCoins(userBalance - betAmount);

        entityManager.persist(bet);
        template.convertAndSend("/queue/"+room,
                format("{0} has increased their bet by {1}", u.getUsername(), bet.getAmount()),
                responseHeader.toMap());

    }

}