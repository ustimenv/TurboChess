package es.ucm.fdi.iw.turbochess.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessagePacket{
    private MessageType type;
    private String text;
    private String from;

    public enum MessageType {
        TEXT,
        JOIN_ROOM,
        LEAVE_ROOM,
        VOTE_KICK
    }

}
