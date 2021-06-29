package turbochess.service.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import turbochess.model.User;
import turbochess.model.chess.Game;
import turbochess.repository.GameRepository;

import java.util.List;

@Service
public class GameServiceImp implements GameService{

    @Autowired
    private GameRepository repository;

    @Override
    public List<Game> getGamesByUser(User user){
        return repository.getGamesByUser(user.getId());
    }

}
