package es.ucm.fdi.iw.turbochess.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class FriendshipRequest {

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

        /*
@NamedQueries({
        /*http://www.it.uc3m.es/jaf/aw/practicas/6-spring/
        busca solicitudes de amistad dados un usuario que las recibe y un
        estado que se proporcionen como parámetros del mismo.
         Devuelve la lista de objetos FriendshipRequest que
         cumplan con el criterio de búsqueda*/
 /*   @NamedQuery(name="friendshipRequest.receiverAndState",
            query="SELECT req.sender_id, req.state FROM friendship_request req "
                    + "WHERE req.receiver_id = :id"),
    /*es similar, pero filtra además por el usuario que la envía.*/
/*    @NamedQuery(name="friendshipRequest.findSerderAndReceiver",
            query="SELECT req.sender_id,req.receiver_id, req.state "
                    + "FROM friendship_request req "
                    + "WHERE req.receiver_id = :id"),
})*/
}
