package turbochess.service.participant;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import turbochess.model.room.Participant;

import java.util.List;

public interface ParticipantRepository extends CrudRepository<Participant, Long>{
    @Query(value = "SELECT p FROM Participant p WHERE p.user.id = :user_id AND p.room.code = :roomCode")
    Participant getParticipant(@Param("roomCode") String roomCode, @Param("user_id") long userID);

    @Query(value = "SELECT p FROM Participant p WHERE p.sessionId = :sessionId")
    Participant getBySessionId(@Param("sessionId") String sessionId);

    @Query(value = "SELECT COUNT(p) FROM Participant p WHERE p.room.code = :roomCode AND p.user.id = :user_id")
    int countParticipants(@Param("roomCode") String roomCode, @Param("user_id") long userID);

    @Query(value = "SELECT p FROM Participant p WHERE p.room.code = :roomCode")
    List<Participant> getRoomParticipants(@Param("roomCode") String roomCode);

    @Query(value = "SELECT p.user.id FROM Participant p WHERE p.room.code = :roomCode AND p.role = :role")
    List <Long> getUserIdsInRoomWithRole(@Param("roomCode") String roomCode, @Param("role") Participant.Role role);

    @Query(value = "SELECT p FROM Participant p WHERE p.user.username = :username AND p.sessionId = null")
    Participant getUnsubscribedParticipantWithName(@Param("username") String username);
}

