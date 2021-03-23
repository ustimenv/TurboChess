package es.ucm.fdi.iw.turbochess.model;
import java.util.List;

import javax.persistence.*;

import lombok.Data;

@Entity
@Data
public class Competition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany
    List<Participant> participants;

}
