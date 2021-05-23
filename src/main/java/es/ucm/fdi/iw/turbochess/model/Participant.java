package es.ucm.fdi.iw.turbochess.model;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
public class Participant {        // User present in a room

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;            // internally (within the roon) assigned id

    @ManyToOne                  // user may participate in many rooms simultaneously
    private User user;

    @ManyToOne
    private Room room;

    @Column(nullable = false)
    private int currentBet=0;

    @Enumerated(EnumType.STRING)
    private Role role;          // we'll store the roles as strings for clarity

    public enum Role{
        PLAYER1, PLAYER2, OBSERVER;
    }
}
