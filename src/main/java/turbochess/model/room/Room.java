package turbochess.model.room;

import lombok.Data;
import turbochess.model.User;
import turbochess.service.room.RoomException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static java.text.MessageFormat.format;

@Entity
@Data
@NoArgsConstructor
public class Room{                           // includes two players and an undetermined number of observers

    @Id
    private String code;

    @Column(nullable = false)
    private @Getter @Setter
    int capacity;            // maximum number of people able to be in a room at any given time, specified by the room's creator (PLAYER_1)

    @Column(name = "num_participants", nullable = false)
    private @Getter int numParticipants=0;


    @Enumerated(EnumType.STRING)
    private @Getter GameState gameState=GameState.NOT_STARTED;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<Participant> participants = new ArrayList<>();

    @Column(name="stored_fen", nullable = true)
    String storedFen;                         // board state, initialised only when the room's creator asks for it

    @Column(name="stored_participants", nullable = true)
    String storedParticipants;                         // json string containing serialised participants (ids, roles, bets)



    public Room(String code, int capacity){
        this.code = code;
        this.capacity = capacity;
    }

    public enum GameState{
        NOT_STARTED, WHITE_TURN, BLACK_TURN, WHITE_WON, BLACK_WON, DRAW
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

    public User getPlayer1(){
        Participant p =participants.stream().filter(u->u.getRole().equals(Participant.Role.PLAYER1)).findFirst()
                .orElse(null);
        return p.getUser();
    }
    public User getPlayer2(){
        Participant p =participants.stream().filter(u->u.getRole().equals(Participant.Role.PLAYER2)).findFirst()
                .orElse(null);
        return p.getUser();
    }

    public int getNumParticipants(){
        return this.numParticipants;
    }
}
