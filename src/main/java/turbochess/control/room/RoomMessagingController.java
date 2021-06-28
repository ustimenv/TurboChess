package turbochess.control.room;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import turbochess.model.User;
import turbochess.model.chess.Bet;
import turbochess.model.chess.Game;
import turbochess.model.chess.Move;
import turbochess.model.messaging.client.*;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;
import turbochess.service.participant.ParticipantException;
import turbochess.service.room.RoomException;

import java.text.MessageFormat;
import java.util.concurrent.ThreadLocalRandom;

import static java.text.MessageFormat.format;

@Controller
public class RoomMessagingController extends RoomController{
    private static Logger log = LogManager.getLogger(RoomMessagingController.class);

    protected String getCheer(String username, String code) throws RoomException{
        Room contextRoom = roomService.getRoomByCode(code);
        Room.GameState state = contextRoom.getGameState();
        String[] cheers={"I'm excited to be here!"};    // failsafe
        if(state == Room.GameState.NOT_STARTED){
            cheers = new String[]{format("{0} can't wait for the game to start!", username),
                    format("{0} says time is money, let's start the game already!", username)};
        } else if(state == Room.GameState.WHITE_TURN){
            cheers = new String[]{format("{0} is awaiting the imminent whites victory!", username),
                    format("Whites really deserve this victory, says {0}!", username)};
        } else if(state == Room.GameState.BLACK_TURN){
            cheers = new String[]{format("Blacks victory isn't far, at least according to {0}", username),
                    format("If whites win, I'll eat my own shoe!, claims {0}", username)};
        }
        return cheers[ThreadLocalRandom.current().nextInt(0, cheers.length)];
    }

    @MessageMapping("/{room}.chat.sendMessage")
    @SendTo("/queue/{room}")
    public ClientPacket sendMessage(@DestinationVariable String room, @Payload TextPacket clientPacket) {
        if(room == null){
            log.error("[sendMessage]: room is null!");
        } else  log.info(format("[sendMessage]: message {0} sent successfully", clientPacket));
        return clientPacket;
    }

    @MessageMapping("/{room}.chat.addUser")
    @SendTo("/queue/{room}")
    public ClientPacket addUser(@DestinationVariable String room, @Payload JoinRoomPacket clientPacket,
                                SimpMessageHeaderAccessor headerAccessor) throws RoomException{
        // Add username in web socket session
        if(room == null || clientPacket.getFrom().length()<1){
            log.error("[addUser]: Failed to add user");
            return null;
        }   log.info(format("[addUser]: joining msg {0} sent successfully", clientPacket));
        headerAccessor.getSessionAttributes().put("username", clientPacket.getFrom());
        Room contextRoom = roomService.getRoomByCode(room);
        clientPacket.setNumParticipants(String.valueOf(contextRoom.getNumParticipants()));
        return clientPacket;
    }
    @MessageMapping("/{room}.chat.cheer")
    @SendTo("/queue/{room}")
    public ClientPacket cheer(@DestinationVariable String room, @Payload TextPacket messagePacket,
                               SimpMessageHeaderAccessor headerAccessor) throws RoomException{
        log.info(format("[cheer]: {0} sent successfully", messagePacket));
        messagePacket.setText(getCheer(messagePacket.getFrom(), room));
        return messagePacket;
    }

    @MessageMapping("/{room}.sys.sendMove")
    @SendTo("/queue/{room}")
    @Transactional
    public ClientPacket sendMove(@DestinationVariable String room, @Payload MovePacket clientPacket) {
        log.info(format("Room [{0}]: move made-->{1}", room, clientPacket));

        if(room == null || clientPacket ==null)	return null;
        // check to see if the sender has the permission to make this move
        for(int i=0; i<10; i++) System.out.println(clientPacket);
        try{
            User user = getUserByUsername(clientPacket.getFrom());
            Room contextRoom = roomService.getRoomByCode(room);
            Participant p = participantService.getParticipantByUsernameAndRoom(contextRoom, user);

            String allegedColour = clientPacket.getColour();
            String fen = clientPacket.getFen();
            String from = clientPacket.getOrigin();
            String to = clientPacket.getDestination();
            Move move = new Move(allegedColour, from, to);

            if(!p.getColourString().equals(allegedColour))
                throw new RoomException(format("Participant {0} with colour {1} can't move for "+allegedColour,
                                                user.getUsername(), p.getColourString()));

            boolean notStarted = contextRoom.getGameState() == Room.GameState.NOT_STARTED;
            boolean whitesTurn = contextRoom.getGameState() == Room.GameState.WHITE_TURN;
            boolean blacksTurn = contextRoom.getGameState() == Room.GameState.BLACK_TURN;

            boolean firstMove = notStarted && p.getColour() == Participant.Colour.WHITE;
            boolean whitesMoving = whitesTurn && p.getColour() == Participant.Colour.WHITE;
            boolean blacksMoving = blacksTurn && p.getColour() == Participant.Colour.BLACK;

            if(firstMove){  // ie the first move has just been made
                contextRoom.setGameState(Room.GameState.BLACK_TURN);
            } else if(whitesMoving){
                contextRoom.setGameState(Room.GameState.BLACK_TURN);
            } else if(blacksMoving){
                contextRoom.setGameState(Room.GameState.WHITE_TURN);
            } else{
                Room.GameState expected;
                if(whitesTurn)       expected = Room.GameState.BLACK_TURN;
                else if(blacksTurn)  expected = Room.GameState.WHITE_TURN;
                else                 expected = null;       // a super invalid game state
                throw new RoomException(format("Invalid move state: expected {0}, received  {1}", expected, allegedColour));
            }
            contextRoom.setCurrentTurn(contextRoom.getCurrentTurn()+1);
            contextRoom.setFen(fen);
            contextRoom.setMoves(contextRoom.getMoves() + Move.DELIMITER + move);
            entityManager.persist(contextRoom);
            log.info(format("User {0} made a move successfully", user.getUsername()));
            return clientPacket;
        } catch(RoomException | ParticipantException e){
            log.error(format("[make move]: {0}", e.getMessage()));
            return null;
        }
    }

    @MessageMapping("/{room}.sys.placeBet")
    @SendTo("/queue/{room}")
    @Transactional
    public ClientPacket placeBet(@DestinationVariable String room, @Payload BetPacket packet){
        log.info(format("[place bet]: received packet{0}", packet));
        try{
            User userFrom = getUserByUsername(packet.getFrom());
            Room contextRoom = roomService.getRoomByCode(packet.getContext());
            Participant p = participantService.getParticipantByUsernameAndRoom(contextRoom, userFrom);

            int userBalance = userFrom.getCoins();
            int betAmount = Integer.parseInt(packet.getBetAmount());

            if(betAmount <= 0)	throw new NumberFormatException("Invalid bet value: " + betAmount);

            if(userBalance < betAmount){
                throw new RoomException(format("Insufficient ({0}) balance ({1}) for player {2}",
                                                            userBalance, betAmount, userFrom.getUsername()));
            }
            Game.Result resultBettedOn;
            if("whites".equals(packet.getBettingOn())){
                resultBettedOn = Game.Result.WHITES_WON;
            } else if("blacks".equals(packet.getBettingOn())){
                resultBettedOn = Game.Result.BLACKS_WON;
            } else if("draw".equals(packet.getBettingOn())){
                resultBettedOn = Game.Result.DRAW;
            } else{
                log.info(format("Invalid betting target {0}", packet.getBettingOn()));
                return null;
            }

            userFrom.setCoins(userBalance - betAmount);
            Bet b = new Bet(p, betAmount, contextRoom.getCurrentTurn(), resultBettedOn);
            entityManager.persist(b);
            log.info(format("Bet of {0} coins has been placed successfully by {1}", betAmount, p.getUser().getUsername()));
            return packet;
        } catch(RoomException | NumberFormatException | ParticipantException e){
            log.error(format("[place bet]: failed to place bet --> {0}", e.getMessage()));
            return null;		// TODO change to 505 or smth
        }
    }

}