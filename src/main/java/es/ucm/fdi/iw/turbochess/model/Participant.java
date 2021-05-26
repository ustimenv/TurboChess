package es.ucm.fdi.iw.turbochess.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
public class Participant {

    public Participant(User user){
        this.user = user;
    }
//    public Participant(User user){        //todo this is the m
//        this.user = user;
//    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;                                    // internally (within the roon) assigned id

    @ManyToOne(fetch = FetchType.LAZY)                  // user may participate multiple room, hence this middleman class
    @JoinColumn(name = "username")
    private User user;

    @ManyToOne                                          // we prohibit players from double-joining rooms
    @JoinColumn(name="code")
    private Room room;

    @Transient
    private int currentBet = 0;                         // on the last transaction we will commit the bet manually
                                                                 // to avoid potentially complex rollback logic

    @Enumerated(EnumType.STRING)
    private Role role;                                  // we'll store the roles as strings for clarity

    public enum Role{
        PLAYER1, PLAYER2, OBSERVER;
    }

    @Override
    public int hashCode(){
        return user.getUsername().hashCode() * Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object other){
        if( !(other instanceof Participant) ){
            return false;
        } else{
            return this.user.getUsername().equals(
                    ((Participant) other).user.getUsername());
        }
    }
}
