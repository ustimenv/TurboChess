package es.ucm.fdi.iw.turbochess.model;

import javax.persistence.*;

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

    @Column(nullable = false)
    int result;

    @Column(nullable = false)
    int secondsLeft;
}