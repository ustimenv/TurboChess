package turbochess.model.messaging.server;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateRoomResponse extends Response{
    private String roomCodeAssigned;
    private String colourAssigned;
}
