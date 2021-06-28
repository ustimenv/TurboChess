package turbochess.model.messaging.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetGamePacket extends ClientPacket{
    private String time;
    private String whites;  // username
    private String blacks;  // username
}
