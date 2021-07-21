package turbochess.service.user;

import org.springframework.stereotype.Service;
import turbochess.model.User;

import java.util.List;

@Service
public interface UserService{
    User getUserByUsername(String username) throws UserException;
    List<User> getByLikeUsername(String username);
    User getUserById(long id) throws UserException;
    boolean doesUserExists(String username);
    List <User> getUserFriends(User user) throws UserException;

    List<User> getAllUsers();
    User save(User user);
}
