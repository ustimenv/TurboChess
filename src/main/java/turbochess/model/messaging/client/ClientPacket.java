package turbochess.model.messaging.client;

/*
*       Base class for packets emitted by the client. Up to individual subclasses to add additional fields as needed
* */

import lombok.Data;
@Data
abstract public class ClientPacket{
    private String context;         // code of the room in whose "context" this message was sent
    private String from;            // username of the user
    protected PacketType type;

}
