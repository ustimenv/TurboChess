package turbochess.service.game;

import org.springframework.stereotype.Service;
import turbochess.model.User;
import turbochess.model.room.Game;

import java.util.List;

@Service
public interface GameService{
    List<Game> getGamesByUser(User user);
    Game save(Game game);
}
