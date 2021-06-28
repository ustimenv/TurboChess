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

//    @Override
//    public List<Game> getGamesByUser(User user){
//        List <Game> asWhites = repository.getUserGamesWhites(user.getId());
//        List <Game> asBlacks = repository.getUserGamesBlacks(user.getId());
//        return Stream.concat(asWhites.stream(), asBlacks.stream())
//                .collect(Collectors.toList());
//    }

//    @Override
//    public List<Game> getGamesByUsers(User whites, User blacks){
//        return repository.getGamesByUsers(whites.getId(), blacks.getId());
//    }

//    @Override
//    public Game getGameByUser(User user){
//        return repository.getGameByUser(user.getId());
//    }
//
//
//    @Override
//    public Game getGameInfoByUser(User user){
//        return repository.getGameByUser(user.getId());
//    }

    @Override
    public List<Game> getGamesInfoByUser(User user){
        return repository.getGamesInfoByUser(user.getId());
    }

    @Override
    public List<String> getGameMovesByGameInfo(Game gameInfo){
        String moves = repository.getGameByGameInfo(gameInfo.getWhites().getId(), gameInfo.getBlacks().getId(),
                                                        String.valueOf(gameInfo.getEndTime()));
        return Game.movesToList(moves);
    }
}
