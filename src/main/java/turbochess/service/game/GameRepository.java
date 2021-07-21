package turbochess.service.game;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import turbochess.model.room.Game;

import java.util.List;

// technically two users may have played dozens of games against one another
public interface GameRepository extends CrudRepository<Game, Long>{
    @Query(value = "SELECT * FROM Game WHERE whites = :userID OR blacks = :userID", nativeQuery = true)
    List<Game> getGamesByUser(@Param("userID") long userID);
}
