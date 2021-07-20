package turbochess.model.messaging;

import lombok.Data;

@Data
public class BetPacket extends ClientPacket{
    private String betAmount;
    private String bettingOn;                           // whites, blacks or draw
    protected PacketType type = PacketType.BET_RAISE;

}
