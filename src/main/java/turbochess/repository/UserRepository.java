package turbochess.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import turbochess.model.User;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long>{
    @Query(value = "SELECT u FROM User u WHERE u.username LIKE :username AND u.enabled = 1")
    User getByUsername(@Param("username") String username);

    @Query(value = "SELECT u FROM User u WHERE u.id = :id")
    User getByUserId(@Param("id") long userId);

    @Query(value = "SELECT COUNT(u) FROM User u WHERE u.username = :username")
    int countUsersWithUsername(@Param("username") String username);

    @Query(value = "SELECT * FROM Friends " +
            "LEFT JOIN User on Friends.friend_id =User.id WHERE Friends.SUBJECT_ID= :userid " +
            "UNION ALL" +
            " SELECT  * FROM Friends LEFT JOIN User on Friends.SUBJECT_ID=User.id WHERE friend_id= :userid", nativeQuery = true)
    List<User> getFriendsByUserId(@Param("userid") long userId);

}