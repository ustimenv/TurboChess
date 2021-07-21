package turbochess.service.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import turbochess.model.User;
import turbochess.model.room.Bet;
import turbochess.model.room.Game;

import java.util.List;

@Service
public class GameService{
    @Autowired
    private GameRepository repository;

    public Game save(Game game){
        return repository.save(game);
    }

    public void delete(Game game){
        repository.delete(game);
    }

    public List<Game> getGamesByUser(User user){
        return repository.getGamesByUser(user.getId());
    }


}
