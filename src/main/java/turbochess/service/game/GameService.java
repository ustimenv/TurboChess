package turbochess.service.game;

import org.springframework.stereotype.Service;
import turbochess.model.User;
import turbochess.model.chess.Game;
import turbochess.model.chess.Move;

import java.util.List;

@Service
public interface GameService{
//    List<Game> getGamesByUsers(User whites, User blacks);

//    Game getGameByUser(User user);
//    List<Game> getGamesByUser(User user);

//    Game getGameInfoByUser(User user);


    List<Game> getGamesByUser(User user);
//    List<String> getGameMovesByGameInfo(Game gameInfo);

}
