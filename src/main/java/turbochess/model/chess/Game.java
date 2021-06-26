package turbochess.model.chess;

import lombok.Data;
import turbochess.model.User;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Entity
@Data
public class Game{
    public enum Result {
        WHITES_WON, BLACKS_WON, DRAW
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
    private Result result;

    @Column
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "whites")
    private User whites;

    @ManyToOne
    @JoinColumn(name = "blacks")
    private User blacks;

    @Column
    private String moves;

    public List <String> getListOfMove(){
        String separator = "|";
        return Arrays.asList(moves.split(Pattern.quote(separator)));
    }
}