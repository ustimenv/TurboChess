package turbochess.model.room;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.regex.Pattern;

@Data
@AllArgsConstructor
public class Move{
    private static Pattern pattern = Pattern.compile("^[a-f][1-8]->[a-f][1-8]$");
    public final static String DELIM = "|";
    private String from, to;
    private String fen;

    public String toString(){
        return from + "->" + to;
    }

    public static boolean isValid(Move move){
        return pattern.matcher(move.toString()).matches();
    }
}
