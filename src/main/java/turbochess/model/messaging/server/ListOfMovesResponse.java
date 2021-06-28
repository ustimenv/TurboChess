package turbochess.model.messaging.server;

public class ListOfMovesResponse extends Response{
    private String[] moves;

    public ListOfMovesResponse(String[] moves){
        this.moves = moves;
    }
}
