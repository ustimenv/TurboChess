package turbochess.model.messaging.server;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JoinRoomResponse extends Response{
    private String colourAssigned;
    private String fen;
    private String accumulatedBet;

}
