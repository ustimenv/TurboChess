package turbochess.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import turbochess.model.User;
import turbochess.model.room.Participant;

import java.util.List;

public interface UserRepository extends CrudRepository<Participant, Long>{
    @Query(value = "SELECT u FROM User u WHERE u.username LIKE :username AND u.enabled = 1")
    User getByUsername(@Param("username") String username);

    @Query(value = "SELECT u FROM User u WHERE u.id = :id")
    User getByUserId(@Param("id") long userId);

    @Query(value = "SELECT COUNT(u) FROM User u WHERE u.username = :username")
    int countUsersWithUsername(@Param("username") String username);


    @Query(value = "SELECT * FROM user_friends LEFT JOIN user on user_friends.friends_id =user.id WHERE user_id= :userid " +
                        "UNION ALL " +
                   "SELECT * FROM user_friends LEFT JOIN user on user_friends.user_id=user.id WHERE friends_id= :userid",
            nativeQuery = true)
    List<User> getFriendsByUserId(@Param("userid") long userId);

}