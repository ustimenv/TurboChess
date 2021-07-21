package turbochess.service.participant;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import turbochess.model.room.Participant;

import java.util.List;

public interface ParticipantRepository extends CrudRepository<Participant, Long>{
    @Query(value = "SELECT p FROM Participant p WHERE user.id = :user_id AND room.code = :roomCode")
    Participant getParticipant(@Param("roomCode") String roomCode, @Param("user_id") long userID);

    @Query(value = "SELECT p FROM Participant p WHERE session_id = :sessionId")
    Participant getBySessionId(@Param("sessionId") String sessionId);

    @Query(value = "SELECT COUNT(p) FROM Participant p WHERE room_code = :roomCode AND user.id = :user_id")
    int countParticipants(@Param("roomCode") String roomCode, @Param("user_id") long userID);

    @Query(value = "SELECT p FROM Participant p WHERE room.code = :roomCode")
    List<Participant> getRoomParticipants(@Param("roomCode") String roomCode);

    @Query(value = "SELECT p.user.id FROM Participant p WHERE room.code = :roomCode AND role = :role")
    List <Long> getUserIdsInRoomWithRole(@Param("roomCode") String roomCode, @Param("role") Participant.Role role);

    @Query(value = "SELECT p FROM Participant p WHERE username = :username AND session_id = 'null'")
    Participant getUnsubscribedParticipantWithName(@Param("username") String username);
}

