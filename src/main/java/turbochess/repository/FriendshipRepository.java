package turbochess.repository;

import org.springframework.data.repository.CrudRepository;
import turbochess.model.Friendship;
import turbochess.model.User;

import java.util.List;

public interface FriendshipRepository extends CrudRepository<Friendship, Integer> {
    Friendship findBySenderAndReceiverAndState(User sender, User receiver, Friendship.State state);
    List<Friendship> findByReceiverAndState(User receiver, Friendship.State state);

}
