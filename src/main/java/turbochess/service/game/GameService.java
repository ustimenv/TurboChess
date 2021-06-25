package turbochess.service.game;

import org.springframework.stereotype.Service;
import turbochess.model.chess.Game;

import java.util.List;

@Service
public interface GameService{
    List<Game> getUserGames(long userId);
    List<Game> getGamesByUsersIds(long whitesId, long blacksId);
}
