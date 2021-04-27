package es.ucm.fdi.iw.turbochess.model;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(targetEntity = User.class)
    private List<User> friends;

    @NotNull
    private String nickname;

    @NotNull
    private String password;

    private String avatar;

    @NotNull
    private int elo;

    //Admin = 1 , Normal user = 0
    @NotNull
    private int permission;
	
}