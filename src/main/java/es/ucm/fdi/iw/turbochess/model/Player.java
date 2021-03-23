package es.ucm.fdi.iw.turbochess.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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

    @NotNull
    boolean isWhite;

    @NotNull
    int result;

    @NotNull
    int secondsLeft;
}
