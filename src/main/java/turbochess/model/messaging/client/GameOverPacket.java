package turbochess.model.messaging.client;

import lombok.Data;

@Data
public class GameOverPacket extends ClientPacket{
    protected PacketType type = PacketType.GAME_OVER;

    private String result;          // WIN, LOSS, DRAW
}
