package turbochess.repository;

import turbochess.model.Friendship;
import turbochess.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FriendshipRepository extends CrudRepository<Friendship, Integer> {
    List<Friendship> findBySenderAndReceiverAndState(User sender, User receiver, Friendship.State state);
    List<Friendship> findByReceiverAndState(User receiver, Friendship.State state);
}
