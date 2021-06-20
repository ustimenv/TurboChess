package es.ucm.fdi.iw.turbochess.model.room;

import es.ucm.fdi.iw.turbochess.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name= "room_code")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Participant(Room room, User user){
        this.room = room;
        this.user = user;
    }

    private int currentBet = 0;             // on the last transaction we will commit the bet manually

    @Enumerated(EnumType.STRING)
    private Role role;                      // we'll store the roles as strings for clarity

    public enum Role{
        PLAYER1, PLAYER2, OBSERVER;
    }

    @Override
    public int hashCode(){
        return room.hashCode()*user.hashCode();
    }

    @Override
    public boolean equals(Object other){
        if( !(other instanceof Participant) ){
            return false;
        } else{
            return this.user.getUsername().equals(((Participant) other).user.getUsername()) &&
                   this.room.getCode().equals(((Participant) other).room.getCode());
        }
    }
}
