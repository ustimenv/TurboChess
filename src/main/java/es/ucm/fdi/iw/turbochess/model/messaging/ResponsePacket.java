package es.ucm.fdi.iw.turbochess.model.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResponsePacket{
    private String payload;

}
