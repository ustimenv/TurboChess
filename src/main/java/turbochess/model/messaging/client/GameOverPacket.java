package turbochess.model.messaging.client;

import lombok.Data;

@Data
public class GameOverPacket extends ClientPacket{
    private String result;          // WIN, LOSS, DRAW
}
