package turbochess.service.friendship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import turbochess.model.Friendship;
import turbochess.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
@Service
public class FriendshipService {
    @PersistenceContext
    //@Autowired
    private EntityManager entityManager;

    @Autowired
    private FriendshipRepository friendshipRepository;

    public Friendship save(Friendship friendship){
        return friendshipRepository.save(friendship);
    }
    public void delete(Friendship friendship){
        delete(friendship);
    }

    public Friendship createFriendshipRequest(User sender, User receiver) throws FriendshipException {
        if (sender.getFriends().contains(receiver)) {
            throw new FriendshipException("Users are friends already");
        } else if (friendshipRepository.findBySenderAndReceiverAndState(sender, receiver, Friendship.State.OPEN) != null ||
                friendshipRepository.findBySenderAndReceiverAndState(receiver, sender, Friendship.State.OPEN) == null){
            throw new FriendshipException("A pending request exists");
        }
        Friendship request;
        //ya existe un decline en alguna peticion previa
        if(friendshipRepository.findBySenderAndReceiverAndState(sender, receiver, Friendship.State.DECLINED) != null){
            request = friendshipRepository.findBySenderAndReceiverAndState(sender, receiver, Friendship.State.DECLINED);
        } else if(friendshipRepository.findBySenderAndReceiverAndState(receiver, sender, Friendship.State.DECLINED) != null){
            request = friendshipRepository.findBySenderAndReceiverAndState(receiver, sender, Friendship.State.DECLINED);
        }else   request = new Friendship();

        request.setSender(sender);
        request.setReceiver(receiver);
        request.setState(Friendship.State.OPEN);
        friendshipRepository.save(request);
        return request;
    }

    /**
     * Comprobar que la solicitud esté todavía en estado OPEN y que el usuario recibido
     * por parámetro sea realmente el usuario receptor de la solicitud de amistad.
     * En caso de error, se lanza la excepción con un mensaje indicando cuál es el problema.
     * Se establece el estado ACCEPTED en la solicitud, así como el sello temporal de la respuesta.
     * Se añade al receptor a la lista de amigos del remitente de la solicitud y viceversa.
     * Mediante la clase de repositorio correspondiente se guardan los tres objetos modificados
     * (los dos usuarios y la solicitud) con el método save.
     * @param request
     * @param receiver
     * @throws FriendshipException
     */
    public void acceptFriendshipRequest(Friendship request, User receiver)
            throws FriendshipException {
        if(request.getReceiver().getId() == receiver.getId()){
            request.setState(Friendship.State.ACCEPTED);
            User send = request.getSender();
            receiver.getFriends().add(send);
            send.getFriends().add(receiver);
            entityManager.flush();
            friendshipRepository.save(request);
        }
    }

    public void declineFriendshipRequest(Friendship request, User receiver) throws FriendshipException {
        if(request.getReceiver() == receiver){
            request.setState(Friendship.State.DECLINED);
            friendshipRepository.save(request);
        }
    }

    public List<Friendship> findByReceiverAndState(User receiver, Friendship.State state) {
        return friendshipRepository.findByReceiverAndState(receiver, state);
    }

    public Friendship findOpenRequestBetween(User sender, User receiver) {
        return friendshipRepository.findBySenderAndReceiverAndState(sender,receiver,Friendship.State.OPEN);
    }
    

}
