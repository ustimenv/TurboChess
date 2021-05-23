package es.ucm.fdi.iw.turbochess.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Room{                        // includes two players and an undetermined number of observers

    @Id
    private final String code;              // a room is uniquely identified by its code
    private final int capacity;             // maximum number of people able to be in a room at any given time, specified
                                            // by the room's creator (PLAYER_1)

    @Enumerated(EnumType.STRING)
    private GameState gameState;

    @OneToMany
    private List <Participant> participants=new ArrayList<>();


    public Room(String code, int capacity){
        this.code=code;
        this.capacity = capacity;
        gameState=GameState.NOT_STARTED;
    }

    public enum GameState{
        NOT_STARTED, WHITE_TURN, BLACK_TURN, WHITE_WON, BLACK_WON, DRAW
    }

    public boolean addParticipant(Participant p){
        if(participants.size() == 0){                       // room creator is player 1
            p.setRole(Participant.Role.PLAYER1);
            participants.add(p);
        } else if(participants.size() == 1){                // 2nd player to join is player 2
            p.setRole(Participant.Role.PLAYER2);
            participants.add(p);
        } else if(participants.size() <= capacity){         // consequent players are automatically observers
            p.setRole(Participant.Role.OBSERVER);
            participants.add(p);
        } else{                                             // players who would result in capacity being exceeded
            return false;                                   // will not be allowed to join
        }
        return true;
    }

}
