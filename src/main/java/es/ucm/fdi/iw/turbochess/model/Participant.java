package es.ucm.fdi.iw.turbochess.model;
import java.util.List;
import javax.validation.constraints.NotNull;
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

    @NotNull
    int points;

}
