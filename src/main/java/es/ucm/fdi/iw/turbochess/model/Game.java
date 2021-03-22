package es.ucm.fdi.iw.turbochess.model;

import java.util.List;
import java.util.ArrayList;
import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Entity
@Table
@Getter
@Setter
@ToString
public class Game{
    @Id
    @SequenceGenerator(
            name = "game_sequence",
            sequenceName = "game_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "game_sequence"
    )
    private Long id;
    @Enumerated(EnumType.STRING)
    private EnumTypeGame typeGame;

    
    private String dateCreation;
    @OneToMany(targetEntity = User.class)
    private List<User> players ;
    private String winner;//nickname
    public Game(Long id,EnumTypeGame type, List<User> players ){
        this.id = id;
        this.typeGame = type;
        this.players = new ArrayList<>();
        this.players = players;
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String strDate = formatter.format(date);
        this.dateCreation = strDate;
    }
    public Game(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String strDate = formatter.format(date);
        this.dateCreation = strDate;
    }
    

}