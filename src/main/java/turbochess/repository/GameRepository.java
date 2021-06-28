package turbochess.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import turbochess.model.chess.Game;

import java.util.List;

// technically two users may have played dozens of games against one another
public interface GameRepository extends CrudRepository<Game, Long>{

//    @Query(value = "SELECT * FROM Game WHERE whites = :userId", nativeQuery = true)
//    List<Game> getUserGamesWhites(@Param("userId") long userId);
//
//    @Query(value = "SELECT * FROM Game WHERE blacks = :userId", nativeQuery = true)
//    List<Game> getUserGamesBlacks(@Param("userId") long userId);

//    @Query(value = "SELECT * FROM Game WHERE whites = :userId AND result=:result", nativeQuery = true)
//    List<Game> getUserGamesByResult(@Param("userId") long userId, @Param("result") String result);

//    @Query(value = "SELECT * FROM Game WHERE whites = :userId AND result=:result", nativeQuery = true)
//    List<Game> getUserGamesWhitesByResult(@Param("userId") long userId, @Param("result") String result);
//
//    @Query(value = "SELECT * FROM Game WHERE blacks = :userId AND result=:result", nativeQuery = true)
//    List<Game> getUserGamesBlacksByResult(@Param("userId") long userId, @Param("result") String result);

//    @Query(value = "SELECT whites blacks result endTime FROM Game WHERE id = :gameID", nativeQuery = true)
//    Game getGameInfoByUser(@Param("gameID") long gameid);

//    @Query(value = "SELECT * FROM Game WHERE whites = :whites AND blacks=:blacks", nativeQuery = true)
//    List<Game> getGamesByUsers(@Param("whites") long whitesID, @Param("blacks") long blacksID);


    @Query(value = "SELECT * FROM Game WHERE whites = :userID OR blacks = :userID", nativeQuery = true)
    List<Game> getGamesByUser(@Param("userID") long userID);
//
//    @Query(value = "SELECT moves FROM Game WHERE whites = :whitesID AND blacks = :blacksID AND end_time = :endTime", nativeQuery = true)
//    String getGameByGameInfo(@Param("whitesID") long whitesId, @Param("blacksID") long blacksId,
//                                 @Param("endTime") String endTime);           // technically we expect a single result


}
