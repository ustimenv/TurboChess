package turbochess.model.chess;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Move{
    private String colour, from, to;

    public String toString(){
        return from + "-" + to;
    }
}
