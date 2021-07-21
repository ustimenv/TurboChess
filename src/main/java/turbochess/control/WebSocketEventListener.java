package turbochess.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;
import turbochess.service.participant.ParticipantException;
import turbochess.service.participant.ParticipantService;
import turbochess.service.room.RoomException;
import turbochess.service.room.RoomService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Component
public class WebSocketEventListener{

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    @Transactional
    public void handleWebSocketConnectListener(SessionConnectedEvent event) throws ParticipantException{
        // in order to subscribe to this room's channel, the user must have joined the room via api/room/join
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Participant subscriber = participantService.getUnsubscribedParticipantInstance(headerAccessor.getUser().getName());
        // if the line above doesn't throw, we known the subscriber had joined the room legally and can therefore subscribe
        subscriber.setSessionId(headerAccessor.getSessionId());
        subscriber.setLastActiveTime(null);
        participantService.save(subscriber);

        SimpMessageHeaderAccessor responseHeader = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        responseHeader.setHeader("FROM", Objects.requireNonNull(headerAccessor.getUser()).getName());
        responseHeader.setHeader("TYPE", "USER_JOINED");
        responseHeader.setUser(headerAccessor.getUser());

        String responseString = subscriber.getUser().getUsername() + " has joined!";
        messagingTemplate.convertAndSend("/queue/"+subscriber.getRoom().getCode(), responseString, responseHeader.toMap());
    }

    @EventListener
    @Transactional
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) throws ParticipantException{
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Participant disconnected = participantService.getParticipantBySessionId(headerAccessor.getSessionId());
        SimpMessageHeaderAccessor responseHeader = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);

        if(disconnected.getRole() == Participant.Role.PLAYER1 || disconnected.getRole() == Participant.Role.PLAYER2){
            // When a player disconnects, start a countdown after which the other player can declare victory on technicality
            responseHeader.setHeader("TYPE", "PLAYER DISCONNECTED");
            disconnected.setLastActiveTime(LocalDateTime.now());
            participantService.save(disconnected);
            responseHeader.setUser(headerAccessor.getUser());
        } else{
            // When an observer disconnects, they lose their observer place so that other users may join the room
            // They are still entitled to winning from placed bet, so don't delete them completely
            disconnected.getRoom().setNumParticipants(disconnected.getRoom().getNumParticipants()-1);
            responseHeader.setHeader("TYPE", "OBSERVER DISCONNECTED");
        }

        responseHeader.setHeader("FROM", disconnected.getUser().getUsername());

        messagingTemplate.convertAndSend("/queue/"+disconnected.getRoom().getCode(),
                                         disconnected.getUser().getUsername()+" disconnected",
                                         responseHeader.toMap());
    }

}
