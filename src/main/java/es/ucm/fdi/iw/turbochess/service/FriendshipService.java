package es.ucm.fdi.iw.turbochess.service;

import es.ucm.fdi.iw.turbochess.model.Friendship;
import es.ucm.fdi.iw.turbochess.model.User;

public interface FriendshipService {
    Friendship createFriendshipRequest(User sender, User receiver)
            throws FriendshipException;

    void acceptFriendshipRequest(Friendship request, User receiver)
            throws FriendshipException;

    void declineFriendshipRequest(Friendship request, User receiver)
            throws FriendshipException;
}
