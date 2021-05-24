package es.ucm.fdi.iw.turbochess.service.imp;

import es.ucm.fdi.iw.turbochess.service.FriendshipException;
import es.ucm.fdi.iw.turbochess.model.Friendship;
import es.ucm.fdi.iw.turbochess.model.User;
import es.ucm.fdi.iw.turbochess.repository.FriendshipRepository;
import es.ucm.fdi.iw.turbochess.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FriendshipServiceImp implements FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Override
    public Friendship createFriendshipRequest(User sender, User receiver)
            throws FriendshipException {
        if (sender.getFriends().contains(receiver)) {
            throw new FriendshipException("Users are friends already");
        } else if (!friendshipRepository
                .findBySenderAndReceiverAndState(sender, receiver, Friendship.State.OPEN).isEmpty()) {
            throw new FriendshipException("A pending request exists");
        } else if (!friendshipRepository
                .findBySenderAndReceiverAndState(receiver, sender, Friendship.State.OPEN).isEmpty()) {
            throw new FriendshipException("A pending request exists");
        }
        Friendship request = new Friendship();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setState(Friendship.State.OPEN);
        friendshipRepository.save(request);
        return request;
    }

    @Override
    public void acceptFriendshipRequest(Friendship request, User receiver)
            throws FriendshipException {
    }

    @Override
    public void declineFriendshipRequest(Friendship request, User receiver)
            throws FriendshipException {
    }
}
