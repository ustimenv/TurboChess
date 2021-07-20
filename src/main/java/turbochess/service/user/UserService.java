package turbochess.service.user;

import org.springframework.stereotype.Service;
import turbochess.model.User;
import turbochess.model.room.Room;

import java.util.List;

@Service
public interface UserService{
    User getUserByUsername(String username) throws UserException;
    User getUserById(long id) throws UserException;

    List <User> getUserFriends(User user) throws UserException;
}
