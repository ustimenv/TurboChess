package turbochess.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import turbochess.model.User;

import java.util.List;

import static java.text.MessageFormat.format;

@Service
public class UserService{
    @Autowired
    private UserRepository repository;

    public User save(User user){
        return repository.save(user);
    }

    public void delete(User user){
        repository.delete(user);
    }

    public User getUserByUsername(String username) throws UserException{
        User u =  repository.getByUsername(username);
        if(u != null){
            return u;
        } else	throw new UserException(format("Unable to find user {0}", username));
    }

    public List<User> getByLikeUsername(String username){
        return repository.getByLikeUsername(username);
    }

    public User getUserById(long id) throws UserException{
        User u = repository.getByUserId(id);
        if(u != null){
            return u;
        } else	throw new UserException(format("Unable to find user with id {0}", id));
    }

    public boolean doesUserExists(String username){
        return repository.countUsersWithUsername(username)>0;
    }

    public List<User> getUserFriends(User user) throws UserException{
        return repository.getFriendsByUserId(user.getId());
    }

    public List<User> getAllUsers(){
        return repository.getAllUsers();
    }

}
