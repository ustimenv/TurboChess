package es.ucm.fdi.iw.turbochess.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Room{                        // includes two players and an undetermined number of observers

    @Id
    private final String code;            // a room is uniquely identified by its code

    @Enumerated(EnumType.STRING)
    private GameResult gameResult;

    @Enumerated(EnumType.STRING)
    private GameType gameType;

    @OneToMany
    private List <Participant> participants=new ArrayList<>();

    public Room(String code){
        this.code=code;
    }

    public enum GameType {
        NORMAL, RANKED, PRIVATE
    }

    public enum GameResult {
        PAUSED, RUNNING, WHITE_WON, BLACK_WON, DRAW
    }
    public void addParticipant(Participant p){
        participants.add(p);
    }

}
