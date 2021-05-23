package es.ucm.fdi.iw.turbochess.model.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class MessagePacket{
    private MessageType type;
    private String payload;
    private String from;        // username of the user

}
