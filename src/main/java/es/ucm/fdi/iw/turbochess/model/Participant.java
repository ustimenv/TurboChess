package es.ucm.fdi.iw.turbochess.model;
import javax.persistence.*;

import lombok.Data;

@Entity
@Data
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    User user;

    @ManyToOne
    Competition competition;

    @Column(nullable = false)
    int points;

}
