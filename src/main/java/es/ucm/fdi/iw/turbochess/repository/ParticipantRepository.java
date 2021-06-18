package es.ucm.fdi.iw.turbochess.repository;

import es.ucm.fdi.iw.turbochess.model.room.Participant;
import es.ucm.fdi.iw.turbochess.model.room.ParticipantKey;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ParticipantRepository extends CrudRepository<ParticipantRepository, ParticipantKey>{
    @Query("SELECT p FROM Participant p WHERE p.room_code = :roomCode AND p.username = :username")
    Participant getParticipant(@Param("roomCode") String roomCode, @Param("username") String username);

    @Query(value = "SELECT COUNT(p) FROM Participant p WHERE p.room_code = :roomCode AND p.username = :username")
    int countParticipants(@Param("roomCode") String roomCode, @Param("username") String username);

    @Modifying
    @Query(value = "DELETE FROM Participant WHERE " +
            "Participant.room_code = :roomCode AND Participant.username = :username", nativeQuery = true)
    void deleteParticipant(@Param("roomCode") String roomCode, @Param("username") String username);

}

