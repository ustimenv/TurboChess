package turbochess.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
/*        /*http://www.it.uc3m.es/jaf/aw/practicas/6-spring/
        busca solicitudes de amistad dados un usuario que las recibe y un
        estado que se proporcionen como parámetros del mismo.
         Devuelve la lista de objetos FriendshipRequest que
         cumplan con el criterio de búsqueda*/
/*@NamedQueries({

        @NamedQuery(name="friendship.receiverAndState",
                query="SELECT sender_id, receiver_id , state FROM Friendship ")
            //            + "WHERE receiver = :id"),
        /*es similar, pero filtra además por el usuario que la envía.*/
      /*  @NamedQuery(name="friendship.findSerderAndReceiver",
                query="SELECT sender_id,receiver_id, state "
                        + "FROM Friendship req "
                        + "WHERE receiver_id = :id"),
})*/
public class Friendship {

    public enum State {
        OPEN,
        ACCEPTED,
        DECLINED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(nullable = false)
    private State state;

}
