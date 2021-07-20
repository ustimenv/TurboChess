package turbochess.service.game;

import org.springframework.stereotype.Service;
import turbochess.model.User;
import turbochess.model.chess.Game;

import java.util.List;

@Service
public interface GameService{
    List<Game> getGamesByUser(User user);
}
