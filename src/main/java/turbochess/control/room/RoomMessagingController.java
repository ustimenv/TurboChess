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
import turbochess.model.messaging.MessagePacket;
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
    public MessagePacket sendMessage(@DestinationVariable String room, @Payload MessagePacket messagePacket) {
        if(room == null){
            log.error("[sendMessage]: room is null!");
        } else  log.info(format("[sendMessage]: message {0} sent successfully", messagePacket));
        return messagePacket;
    }

    @MessageMapping("/{room}.chat.addUser")
    @SendTo("/queue/{room}")
    public MessagePacket addUser(@DestinationVariable String room, @Payload MessagePacket messagePacket,
                                 SimpMessageHeaderAccessor headerAccessor) throws RoomException{
        // Add username in web socket session
        if(room == null || messagePacket.getFrom().length()<1){
            log.error("[addUser]: Failed to add user");
            return null;
        }   log.info(format("[addUser]: joining msg {0} sent successfully", messagePacket));
        headerAccessor.getSessionAttributes().put("username", messagePacket.getFrom());
        Room contextRoom = roomService.getRoomByCode(room);
        messagePacket.setPayload(String.valueOf(contextRoom.getNumParticipants()));
        return messagePacket;
    }

    @MessageMapping("/{room}.sys.sendMove")
    @SendTo("/queue/{room}")
    @Transactional
    public MessagePacket sendMove(@DestinationVariable String room, @Payload MessagePacket messagePacket) {
        log.info(format("Room [{0}]: move made-->{1}", room, messagePacket));

        if(room == null || messagePacket==null)	return null;
        // check to see if the sender has the permission to make this move
        try{
            User user = getUserByUsername(messagePacket.getFrom());
            Room contextRoom = roomService.getRoomByCode(room);
            Participant p = participantService.getParticipantByUsernameAndRoom(contextRoom, user);

            ObjectNode payload = new ObjectMapper().readValue(messagePacket.getPayload(), ObjectNode.class);
            String allegedColour = String.valueOf(payload.get("color")).replaceAll("\"", "");

            if(!p.getColourString().equals(allegedColour))
                throw new RoomException(format("Participant {0} with colour {1} can't move for {2}", user, p.getColourString(), allegedColour));

            boolean notStarted = contextRoom.getGameState() == Room.GameState.NOT_STARTED;
            boolean whitesTurn = contextRoom.getGameState() == Room.GameState.WHITE_TURN;
            boolean blacksTurn = contextRoom.getGameState() == Room.GameState.BLACK_TURN;

            boolean firstMove = notStarted && p.getColour() == Participant.Colour.WHITE;
            boolean whitesMoving = whitesTurn && p.getColour() == Participant.Colour.WHITE;
            boolean blacksMoving = blacksTurn && p.getColour() == Participant.Colour.BLACK;

            if(firstMove){  // ie the first move has just been made
                roomService.setGameState(contextRoom.getCode(), Room.GameState.BLACK_TURN);
            } else if(whitesMoving){
                roomService.setGameState(contextRoom.getCode(), Room.GameState.BLACK_TURN);
            } else if(blacksMoving){
                roomService.setGameState(contextRoom.getCode(), Room.GameState.WHITE_TURN);
            } else{
                Room.GameState expected;
                if(whitesTurn)       expected = Room.GameState.BLACK_TURN;
                else if(blacksTurn)  expected = Room.GameState.WHITE_TURN;
                else                 expected = null;       // a super invalid game state
                throw new RoomException(format("Invalid move state: expected {0}, received  {1}",expected, allegedColour));
            }
            log.info(format("User {0} made a move successfully", user.getUsername()));
            return messagePacket;
        } catch(RoomException | JsonProcessingException | ParticipantException e){
            log.error(format("[make move]: {0}", e.getMessage()));
            return null;
        }
    }

    @MessageMapping("/{room}.sys.placeBet")
    @SendTo("/queue/{room}")
    @Transactional
    public MessagePacket placeBet(@DestinationVariable String room, @Payload MessagePacket packet){
        log.info(format("[place bet]: received packet{0}", packet));
        try{
            User userFrom = getUserByUsername(packet.getFrom());
            Room contextRoom = roomService.getRoomByCode(packet.getContext());
            Participant p = participantService.getParticipantByUsernameAndRoom(contextRoom, userFrom);

            int userBalance = userFrom.getCoins();
            int betAmount = Integer.parseInt(packet.getPayload());

            if(betAmount <= 0)	throw new NumberFormatException("Invalid bet value: " + betAmount);

            if(userBalance < betAmount){
                throw new RoomException(format("Insufficient ({0}) balance ({1}) for player {2}",
                        userBalance, betAmount, userFrom.getUsername()));
            }
            userFrom.setCoins(userBalance-betAmount);
//            removeUserCoins(userFrom, betAmount);
            p.setCurrentBet(p.getCurrentBet()+betAmount);
            log.info(format("Bet of {0} coins has been placed successfully by {1}", betAmount, p.getUser().getUsername()));
            return packet;
        } catch(RoomException | NumberFormatException | ParticipantException e){
            log.error(format("[place bet]: failed to place bet --> {0}", e.getMessage()));
            return null;		// TODO change to 505 or smth
        }
    }

}
