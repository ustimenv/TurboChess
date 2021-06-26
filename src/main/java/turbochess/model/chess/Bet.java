package turbochess.model.chess;

import lombok.Data;
import turbochess.model.room.Participant;

import javax.persistence.*;

@Entity
@Data
public class Bet{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "better")//, referencedColumnName = "id")
    private Participant better;

    @Column(name = "value")
    private int value = 0;

    @Column(name = "turn_placed")
    private int turnPlaced = 0;

    public Bet(Participant better, int value, int turnPlaced){
        this.better = better;
        this.value = value;
        this.turnPlaced = turnPlaced;
    }

}
