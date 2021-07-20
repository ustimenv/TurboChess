package turbochess.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import turbochess.model.User;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;
import turbochess.repository.ParticipantRepository;
import turbochess.repository.UserRepository;
import turbochess.service.room.RoomException;

import java.util.List;

import static java.text.MessageFormat.format;

@Service
public class UserServiceImp implements UserService{

    @Autowired
    private UserRepository repository;

    public User getUserByUsername(String username) throws UserException{
        User u =  repository.getByUsername(username);
        if(u != null){
            return u;
        } else	throw new UserException(format("Unable to find user {0}", username));
    }

    @Override
    public User getUserById(long id) throws UserException{
        User u = repository.getByUserId(id);
        if(u != null){
            return u;
        } else	throw new UserException(format("Unable to find user with id {0}", id));
    }

    @Override
    public List<User> getUserFriends(User user) throws UserException{
        return repository.getFriendsByUserId(user.getId());
    }


}
