package turbochess.model.chess;

import lombok.Data;
import turbochess.model.User;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Entity
@Data
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

    public Game(User whites, User blacks, LocalDateTime endTime, Result result){    // Game info 1
        this(whites, blacks, endTime, result, null);
    }

    public Game(User whites, User blacks, String endTime){                          // Game info 2
        this(whites, blacks, LocalDateTime.parse(endTime, Game.dateTimeFormatter), null, null);
    }

    @Enumerated(EnumType.STRING)
    private Result result;

    @Column(nullable = false)
    private LocalDateTime endTime;

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public String endTimeToString(){
        return endTime.format(dateTimeFormatter);
    }

    @ManyToOne
    @JoinColumn(name = "whites")
    private User whites;

    @ManyToOne
    @JoinColumn(name = "blacks")
    private User blacks;

    @Column(nullable = true)
    private String moves;



    public static List <String> movesToList(String moves){
        if(moves == null || moves.length() < 4)   return List.of();
        return Arrays.asList(moves.split(Pattern.quote(Move.DELIMITER)));
    }
}