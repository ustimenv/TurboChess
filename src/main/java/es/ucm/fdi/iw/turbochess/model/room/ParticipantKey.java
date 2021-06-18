package es.ucm.fdi.iw.turbochess.model.room;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
public class ParticipantKey implements Serializable{
    @Column(name = "room_code", nullable = false)
    private String room_code;

    @Column(name = "username", nullable = false)
    private String username;

}
