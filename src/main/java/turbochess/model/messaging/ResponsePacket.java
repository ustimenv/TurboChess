package turbochess.model.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResponsePacket{
    private String header;
    private String payload;
}
