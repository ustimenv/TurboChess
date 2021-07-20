package turbochess.model.messaging;

import lombok.Data;

@Data
public class MovePacket extends ClientPacket{
    protected PacketType type = PacketType.MOVE;
    private String origin, destination;
    private String colour;
    private String fen;
}
