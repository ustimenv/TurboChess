package es.ucm.fdi.iw.turbochess.model.room;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
public class Room{                           // includes two players and an undetermined number of observers

    @Id
    @Getter @Setter
    private String code;

    @Column(nullable = false)
    private @Getter @Setter
    int capacity;            // maximum number of people able to be in a room at any given time,
                                             // specified by the room's creator (PLAYER_1)

    @Column(name = "num_participants", nullable = false)
    private @Getter int numParticipants=0;

    @Enumerated(EnumType.STRING)
    private @Getter GameState gameState=GameState.NOT_STARTED;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private Set <Participant> participants = new HashSet<>();


    public Room(String code, int capacity){
        this.code = code;
        this.capacity = capacity;
    }

    public enum GameState{
        NOT_STARTED, WHITE_TURN, BLACK_TURN, WHITE_WON, BLACK_WON, DRAW
    }

    public boolean addParticipant(Participant p){
        if(numParticipants == 0){                       // room creator is player 1
            p.setRole(Participant.Role.PLAYER1);
            participants.add(p);
            numParticipants++;
        } else if(numParticipants == 1){                // 2nd player to join is player 2
            p.setRole(Participant.Role.PLAYER2);
            participants.add(p);
            numParticipants++;
        } else if(numParticipants <= capacity){         // consequent players are automatically observers
            p.setRole(Participant.Role.OBSERVER);
            participants.add(p);
            numParticipants++;
        } else{                                             // players who would result in capacity being exceeded
            return false;                                   // will not be allowed to join
        }
        return true;
    }

    public boolean removeParticipant(Participant p){
        boolean wasPresent = participants.remove(p);
        if(wasPresent)  numParticipants--;
        return wasPresent;
    }


    @Override
    public int hashCode(){
        return code.hashCode();
    }

    @Override
    public boolean equals(Object other){
        if( !(other instanceof Room) ){
            return false;
        } else{
            return this.code.equals(((Room) other).getCode());
        }
    }
}
