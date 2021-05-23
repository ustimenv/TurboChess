package es.ucm.fdi.iw.turbochess.model.messaging;

import lombok.Data;

@Data
public class MessagePacket{
    private String room;        // code of the room in whose "context" this message had been sent
    private MessageType type;   // TODO delete this, no longer necessary
    private String payload;
    private String from;        // username of the user
}
