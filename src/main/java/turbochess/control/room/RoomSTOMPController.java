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
import turbochess.service.room.RoomException;

import java.text.MessageFormat;
import java.util.concurrent.ThreadLocalRandom;

import static java.text.MessageFormat.format;

@Controller
public class RoomSTOMPController extends RoomController{
    private static Logger log = LogManager.getLogger(RoomSTOMPController.class);

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
    @MessageMapping("/{room}.chat.cheer")
    @SendTo("/queue/{room}")
    public MessagePacket cheer(@DestinationVariable String room, @Payload MessagePacket messagePacket,
                                 SimpMessageHeaderAccessor headerAccessor) throws RoomException{
        log.info(format("[cheer]: {0} sent successfully", messagePacket));
        messagePacket.setPayload(getCheer(messagePacket.getFrom(), room));
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
            Participant p = getParticipantByUsernameAndRoom(user, contextRoom);

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
        } catch(RoomException | JsonProcessingException e){
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
            Participant p = getParticipantByUsernameAndRoom(userFrom, contextRoom);

            int userBalance = userFrom.getCoins();
            int betAmount = Integer.parseInt(packet.getPayload());

            if(betAmount <= 0)	throw new NumberFormatException("Invalid bet value: " + betAmount);

            if(userBalance < betAmount){
                throw new RoomException(format("Insufficient ({0}) balance ({1}) for player {2}",
                        userBalance, betAmount, userFrom.getUsername()));
            }
            removeUserCoins(userFrom, betAmount);
            increaseParticipantBetBy(userFrom, contextRoom, betAmount);
            log.info(format("Bet of {0} coins has been placed successfully by {1}", betAmount, p.getUser().getUsername()));
            return packet;
        } catch(RoomException | NumberFormatException e){
            log.error(format("[place bet]: failed to place bet --> {0}", e.getMessage()));
            return null;		// TODO change to 505 or smth
        }
    }

}
