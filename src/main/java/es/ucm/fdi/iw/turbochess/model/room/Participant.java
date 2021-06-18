package es.ucm.fdi.iw.turbochess.model.room;

import es.ucm.fdi.iw.turbochess.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
public class Participant {
    @EmbeddedId
    private ParticipantKey key;             // participant is uniquely identified by the (username, roomcode) tuple

    @ManyToOne
  //  @JoinColumn(name = "room_code")
    @MapsId("room_code")
    private Room room;

    @ManyToOne
    @MapsId("username")
//    @JoinColumn(name = "username")
    private User user;

    private int currentBet = 0;             // on the last transaction we will commit the bet manually

    @Enumerated(EnumType.STRING)
    private Role role;                      // we'll store the roles as strings for clarity

    public enum Role{
        PLAYER1, PLAYER2, OBSERVER;
    }

    @Override
    public int hashCode(){
        return key.hashCode();
    }

    @Override
    public boolean equals(Object other){
        if( !(other instanceof Participant) ){
            return false;
        } else{
            return this.key.getUsername().equals(
                    ((Participant) other).key.getUsername());
        }
    }
}
