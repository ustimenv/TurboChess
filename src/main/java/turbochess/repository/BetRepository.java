package turbochess.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import turbochess.model.chess.Bet;

import java.util.List;

public interface BetRepository extends CrudRepository<Bet, Long>{
    @Query(value = "SELECT b FROM Bet b WHERE b.better.id = :participantID")
    List<Bet> getParticipantBets(@Param("participantID") long participantID);

    @Modifying
    @Query(value = "DELETE FROM Bet b WHERE b.better.id = :participantID")
    void deleteBetsByParticipant(@Param("participantID") long participantID);

    @Modifying
    @Query(value = "DELETE FROM Bet b WHERE b.better.room.code = :code")
    void deleteRoomBets(@Param("code") String roomCode);

}
