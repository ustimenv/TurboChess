package turbochess.model.room;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import turbochess.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor

@NamedNativeQueries({
        @NamedNativeQuery(name="Participant.getByUserIdAndRoomCode",
                query= "SELECT * FROM Participant WHERE user_id = :user_id AND room_code = :code", resultClass = Participant.class),

        @NamedNativeQuery(name="Participant.getRoleByUserIdAndRoomCode",
                query= "SELECT role FROM Participant WHERE user_id = :user_id AND room_code = :code"),

        @NamedNativeQuery(name="Participant.getColourByUserIdAndRoomCode",
                query= "SELECT colour FROM Participant WHERE user_id = :user_id AND room_code = :code"),

        @NamedNativeQuery(name="Participant.getRoomParticipants",
                query= "SELECT * WHERE Participant WHERE room_code = :code"),

        @NamedNativeQuery(name="Participant.increaseBetAmountBy",
                query= "UPDATE Participant SET current_bet = current_bet + :betAmount WHERE user_id = :user_id AND room_code = :code"),

})
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

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

    @Enumerated(EnumType.STRING)
    private Colour colour;


    public enum Colour{
        WHITE, BLACK, NONE;
    }
    public String getColourString(){
        switch (colour){
            case WHITE: return "w";
            case BLACK: return "b";
            default:    return "-";
        }
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

    public String toJSON(){
        try{
            return new ObjectMapper().writeValueAsString(this);
        } catch(JsonProcessingException e){
            e.printStackTrace();
            return null;
        }
    }
}
