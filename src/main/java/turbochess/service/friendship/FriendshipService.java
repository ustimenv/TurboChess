package turbochess.service.friendship;

import turbochess.model.Friendship;
import turbochess.model.User;

import java.util.List;

public interface FriendshipService {
    Friendship createFriendshipRequest(User sender, User receiver) throws FriendshipException;

    void acceptFriendshipRequest(Friendship request, User receiver) throws FriendshipException;

    void declineFriendshipRequest(Friendship request, User receiver) throws FriendshipException;

    List<Friendship> findByReceiverAndState(User user, Friendship.State open);

    Friendship findOpenRequestBetween(User sender, User receiver);

    Friendship save(Friendship friendship);
}
