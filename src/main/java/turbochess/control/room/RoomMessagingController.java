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
import turbochess.model.chess.Move;
import turbochess.model.messaging.client.*;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;
import turbochess.service.participant.ParticipantException;
import turbochess.service.room.RoomException;

import static java.text.MessageFormat.format;

@Controller
public class RoomMessagingController extends RoomController{
    private static Logger log = LogManager.getLogger(RoomMessagingController.class);

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
            contextRoom.setFen(fen);
            contextRoom.setMoves(contextRoom.getMoves()+"|"+move);
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
            userFrom.setCoins(userBalance-betAmount);
            p.setCurrentBet(p.getCurrentBet()+betAmount);
            log.info(format("Bet of {0} coins has been placed successfully by {1}", betAmount, p.getUser().getUsername()));
            return packet;
        } catch(RoomException | NumberFormatException | ParticipantException e){
            log.error(format("[place bet]: failed to place bet --> {0}", e.getMessage()));
            return null;		// TODO change to 505 or smth
        }
    }

}
