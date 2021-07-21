package turbochess.model.room;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Move{
    public final static String DELIM = "|";
    private String from, to;
    private String fen;

    public String toString(){
        return from + "->" + to;
    }
}
