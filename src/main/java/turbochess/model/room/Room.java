package turbochess.model.room;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import turbochess.service.room.RoomException;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.text.MessageFormat.format;

@Entity
@Data
@NoArgsConstructor
public class Room{                           // includes two players and an undetermined number of observers

    @Id
    @JsonProperty("code")
    private String code;

    @Column(nullable = false)
    @JsonProperty("capacity")
    private int capacity;            // maximum number of people able to be in a room at any given time, specified by the room's creator (PLAYER_1)

    @Column(name = "num_participants", nullable = false)
    @JsonProperty("numParticipants")
    private int numParticipants=0;


    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private GameState gameState = GameState.NOT_STARTED;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Participant> participants = new ArrayList<>();

    @Column(name="moves", nullable = false)
    @JsonIgnore
    private String moves="";

    @Column(name="fen", nullable =false)
    @JsonIgnore
    private String fen="";

    @Column(name="date_created", columnDefinition = "TIMESTAMP")
    @JsonProperty("dateCreated")
    private LocalDateTime dateCreated;

    @Column(name="current_turn", nullable = false)
    @JsonProperty("currentTurn")
    private int currentTurn=0;                         // json string containing serialised participants (ids, roles, bets)

    public Room(String code, int capacity){
        this.code = code;
        this.capacity = capacity;
        this.dateCreated = LocalDateTime.now();
    }

    public enum GameState{
        NOT_STARTED, WHITES_TURN, BLACKS_TURN,
        DRAW_PROPOSED, WHITES_VICTORY_ALLEGED, BLACKS_VICTORY_ALLEGED
    }

    public Participant.Role assignRole(Participant p) throws RoomException {
        if(participants.contains(p) || participants.size() > capacity){
            throw new RoomException(format("Unable to add participant {0} to room {1}", p, this));
        }
        switch(numParticipants){
            case 0:     return Participant.Role.PLAYER1;
            case 1:     return Participant.Role.PLAYER2;
            default:    return Participant.Role.OBSERVER;
        }
    }

    public void addParticipant(Participant p){
        numParticipants++;
        participants.add(p);
    }

    public void removeParticipant(Participant p) throws RoomException{
        if(!participants.remove(p)){
            throw new RoomException(format("Unable to remove participant {0} from room {1}; participant not present in the room", p, this));
        } else  numParticipants--;
    }

    public boolean isBelowCapacity(){
        return numParticipants < capacity;
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
