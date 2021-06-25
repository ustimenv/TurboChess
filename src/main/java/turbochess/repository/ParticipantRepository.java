package turbochess.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;

import java.util.List;


// this repository is strictly for utility purposes, it does not and must not contain modifying queries, since all
// of them are bound to the room repository
public interface ParticipantRepository extends CrudRepository<Participant, Long>{
    @Query(value = "SELECT p FROM Participant p WHERE p.user.id = :user_id AND p.room.code = :roomCode")
    Participant getParticipant(@Param("roomCode") String roomCode, @Param("user_id") long userID);

    @Query(value = "SELECT COUNT(p) FROM Participant p WHERE room_code = :roomCode AND p.user.id = :user_id")
    int countParticipants(@Param("roomCode") String roomCode, @Param("user_id") long userID);

    @Query(value = "SELECT p FROM Participant p WHERE p.room.code = :roomCode")
    List<Participant> getRoomParticipants(@Param("roomCode") String roomCode);

}

