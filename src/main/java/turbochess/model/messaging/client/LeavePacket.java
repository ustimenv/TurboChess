package turbochess.model.messaging.client;

import lombok.Data;

public class LeavePacket extends ClientPacket{
    protected PacketType type = PacketType.LEAVE_ROOM;

}
