package turbochess.model.room;

import java.util.List;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.persistence.*;

import com.sun.jmx.remote.internal.ArrayQueue;
import turbochess.model.Player;
import lombok.Data;
@Entity
@Data

public class Game{

    public enum GameType {
        NORMAL, RANKED, PRIVATE
    }

    public enum GameResult {
        PAUSED, RUNNING, WHITE_WON, BLACK_WON, DRAW
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private GameType gameType;

     private String room_code;
    
    private GameResult gameResult;

    private LocalDateTime creationDate;

    @OneToMany
    private List<Player> players = new ArrayList<>();

    //FEN notation [FEN "r2qk2r/pbp1bpp1/1pnp3p/8/3NP3/2P3P1/PP1N1PP1/R2QKB1R w KQkq - 1 12"]
    private String gameState;

    //seconds
    private int time ;
// Hashmap <gameState, Player.user.nickname>>
@Column
@ElementCollection(targetClass=String.class)
    private List<String> moves = new ArrayList<String>();

    public void setPlayers(Player p) {
        this.players.add(p);
    }

    public void addMove(String move){
        this.moves.add(move);
    }
}