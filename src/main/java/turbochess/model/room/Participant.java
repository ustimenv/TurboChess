package turbochess.model.room;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turbochess.model.User;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name= "room_code")
    private Room room;

    @Column(unique = true)
    String sessionId;

    @Column(nullable = true)
    private LocalDateTime lastActiveTime;   // takes a non-null value only when the socket closes


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Participant(Room room, User user){
        this.room = room;
        this.user = user;
    }

    @OneToMany(mappedBy = "better", fetch = FetchType.LAZY)
    private List<Bet> bets = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Role role;                      // we'll store the roles as strings for clarity

    public enum Role implements Serializable{
        PLAYER1, PLAYER2, OBSERVER;
    }

    @Enumerated(EnumType.STRING)
    private Colour colour;


    public enum Colour implements Serializable{
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
}
