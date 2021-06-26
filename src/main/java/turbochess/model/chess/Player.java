package turbochess.model.chess;

import javax.persistence.*;

import turbochess.model.User;
import lombok.Data;

@Entity
@Data
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    User user;

    @OneToOne
    Game game;

    @Column(nullable = false)
    boolean isWhite;

    int result;

    int secondsLeft;
}
