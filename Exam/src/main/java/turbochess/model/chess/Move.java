package turbochess.model.chess;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Move{
    public static final String DELIMITER = "|";         // delimits individual moves when they are aggregated in a string
    public static final String SRC_DST_DELIMITER = "-"; // delimits 'from' and 'to'
    private String colour, from, to;
    public String toString(){
        return from + "-" + to;
    }
}
