package turbochess.model.messaging.client;

import lombok.Data;

@Data
public class CreateRoomPacket extends ClientPacket{
    protected PacketType type = PacketType.CREATE_ROOM;
    private String capacity;

}
