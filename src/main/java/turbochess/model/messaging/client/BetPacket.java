package turbochess.model.messaging.client;

import lombok.Data;

@Data
public class BetPacket extends ClientPacket{
    private String betAmount;
    protected PacketType type = PacketType.BET_RAISE;

}
