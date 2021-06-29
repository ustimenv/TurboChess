package turbochess.model.messaging;

import lombok.Data;

@Data
public class JoinRoomPacket extends ClientPacket{
    protected PacketType type = PacketType.JOIN_ROOM;
    private String roomToJoin;
    private String numParticipants;

}
