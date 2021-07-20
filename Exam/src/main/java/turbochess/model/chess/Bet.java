package turbochess.model.chess;

import lombok.Data;
import lombok.NoArgsConstructor;
import turbochess.model.room.Participant;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Bet{
    private static int MAX_WIN = 500;        // max coins to be won from a single bet
    private static double BASE_RETURN = 1;   // this, along with how early the bet was placed, determines the final return

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "better")
    private Participant better;

    @Column(name = "value")
    private int value = 0;

    @Column(name = "turn_placed")
    private int turnPlaced = 0;

    @Enumerated(EnumType.STRING)
    private Game.Result result;                      // we'll store the roles as strings for clarity


    public Bet(Participant better, int value, int turnPlaced, Game.Result result){
        this.better = better;
        this.value = value;
        this.turnPlaced = turnPlaced;
        this.result = result;
    }

    public static int returnOnBet(int value, int turnPlaced, int totalTurns){
        double betEarliness = (double) turnPlaced / (double) totalTurns;    // earlier bets yield higher returns
        return (int) Math.min(MAX_WIN, value * (betEarliness+BASE_RETURN)); // eg min(MAX_WIN, 1.2), for bet placed on turn 2 in a game of 10 turns
    }
}
