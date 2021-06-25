package turbochess.model.messaging.client;

import lombok.Data;

@Data
public class TextPacket extends ClientPacket{
    protected PacketType type = PacketType.TEXT;
    private String text;
}
