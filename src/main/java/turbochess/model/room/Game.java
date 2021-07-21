package turbochess.model.room;

import lombok.Getter;
import lombok.NoArgsConstructor;
import turbochess.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@NoArgsConstructor
public class Game{
    public enum Result {
        WHITES_WON, BLACKS_WON, DRAW;

        public static String valueToString(Result result){
            switch(result){
                case WHITES_WON:    return "Whites won";
                case BLACKS_WON:    return "Blacks won";
                case DRAW:          return "Draw";
            }
            return "Result unknown";
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    public Game(User whites, User blacks, LocalDateTime endTime, Result result, String moves){
        this.whites = whites;
        this.blacks = blacks;
        this.endTime = endTime;
        this.result = result;
        this.moves = moves;
    }

    @Enumerated(EnumType.STRING)
    private @Getter Result result;

    @Column(nullable = false)
    private @Getter LocalDateTime endTime;

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public String getEndTime(){
        return endTime.format(dateTimeFormatter);
    }

    @ManyToOne
    @JoinColumn(name = "whites")
    private @Getter User whites;

    @ManyToOne
    @JoinColumn(name = "blacks")
    private @Getter User blacks;

    @Column(nullable = true)
    private String moves;

    public String getMoves(){
        return moves;
    }

    public String getVictor(){
        if(result == Result.BLACKS_WON){
            return blacks.getUsername()+" won";
        } else if(result == Result.WHITES_WON){
            return whites.getUsername()+" won";
        } else{
            return "Draw";
        }
    }
}