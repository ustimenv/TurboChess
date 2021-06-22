package turbochess.model.messaging;

import lombok.Data;

@Data
public class MessagePacket{
    private String context;        // code of the room in whose "context" this message had been sent
    private MessageType type;
    private String payload;
    private String from;        // username of the user
}
