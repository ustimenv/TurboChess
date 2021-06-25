package turbochess.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import turbochess.model.chess.Game;

import java.util.List;

// technically two users may have played dozens of games against one another
public interface GameRepository extends CrudRepository<Game, Long>{

    @Query(value = "SELECT * FROM Game WHERE whites = :userId", nativeQuery = true)
    List<Game> getUserGamesWhites(@Param("userId") long userId);

    @Query(value = "SELECT * FROM Game WHERE blacks = :userId", nativeQuery = true)
    List<Game> getUserGamesBlacks(@Param("userId") long userId);

    @Query(value = "SELECT * FROM Game WHERE whites = :userId AND result=:result", nativeQuery = true)
    List<Game> getUserGamesWhitesByResult(@Param("userId") long userId, @Param("result") String result);

    @Query(value = "SELECT * FROM Game WHERE blacks = :userId AND result=:result", nativeQuery = true)
    List<Game> getUserGamesBlacksByResult(@Param("userId") long userId, @Param("result") String result);

    @Query(value = "SELECT * FROM Game WHERE whites = :whites AND blacks=:blacks", nativeQuery = true)
    List<Game> getGamesByUsers(@Param("whites") long whitesID, @Param("blacks") long blacksID);

    @Modifying
    @Query(value = "DELETE FROM Game WHERE id = :id", nativeQuery = true)
    void deleteGameByID(@Param("id") long gameID);

}
