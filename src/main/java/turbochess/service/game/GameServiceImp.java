package turbochess.service.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import turbochess.model.chess.Game;
import turbochess.model.room.Room;
import turbochess.repository.GameRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.text.MessageFormat.format;

@Service
public class GameServiceImp implements GameService{

    @Autowired
    private GameRepository repository;

    @Override
    public List<Game> getUserGames(long userId){
        List <Game> asWhites = repository.getUserGamesWhites(userId);
        List <Game> asBlacks = repository.getUserGamesBlacks(userId);
        return Stream.concat(asWhites.stream(), asBlacks.stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<Game> getGamesByUsersIds(long whitesId, long blacksId){
        return repository.getGamesByUsers(whitesId, blacksId);
    }
}
