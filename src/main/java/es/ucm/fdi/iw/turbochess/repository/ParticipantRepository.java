package es.ucm.fdi.iw.turbochess.repository;

import es.ucm.fdi.iw.turbochess.model.room.Participant;
import es.ucm.fdi.iw.turbochess.model.room.ParticipantKey;
import es.ucm.fdi.iw.turbochess.model.room.Room;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParticipantRepository extends CrudRepository<Participant, ParticipantKey>{
    @Query(value = "SELECT * FROM Participant WHERE room_code = :roomCode AND username = :username", nativeQuery = true)
    Participant getParticipant(@Param("roomCode") String roomCode, @Param("username") String username);

    @Query(value = "SELECT COUNT(*) FROM Participant WHERE room_code = :roomCode AND username = :username", nativeQuery = true)
    int countParticipants(@Param("roomCode") String roomCode, @Param("username") String username);

    @Query(value = "SELECT * FROM Participant WHERE room_code = :roomCode", nativeQuery = true)
    List<Participant> getRoomParticipants(@Param("roomCode") String roomCode);

    @Query(value = "SELECT * FROM Participant WHERE username = :username", nativeQuery = true)
    List<Room> getRoomsPresent(@Param("username") String username);

    @Modifying
    @Query(value = "DELETE FROM Participant WHERE " +
            "Participant.room_code = :roomCode AND Participant.username = :username", nativeQuery = true)
    void deleteParticipant(@Param("roomCode") String roomCode, @Param("username") String username);


}

