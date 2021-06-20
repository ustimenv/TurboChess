//package es.ucm.fdi.iw.turbochess.repository;
//
//import Participant;
//import es.ucm.fdi.iw.turbochess.model.room.ParticipantKey;
//import Room;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.CrudRepository;
//import org.springframework.data.repository.query.Param;
//
//import java.util.List;
//
//
//// this repository is strictly for utility purposes, it does not and must not contain modifying queries, since all
//// of them are bound to the room repository
//public interface ParticipantRepository extends CrudRepository<Participant, ParticipantKey>{
//    @Query(value = "SELECT * FROM Participant WHERE room_code = :roomCode AND username = :username", nativeQuery = true)
//    Participant getParticipant(@Param("roomCode") String roomCode, @Param("username") String username);
//
//    @Query(value = "SELECT COUNT(*) FROM Participant WHERE room_code = :roomCode AND username = :username", nativeQuery = true)
//    int countParticipants(@Param("roomCode") String roomCode, @Param("username") String username);
//
//    @Query(value = "SELECT * FROM Participant WHERE room_code = :roomCode", nativeQuery = true)
//    List<Participant> getRoomParticipants(@Param("roomCode") String roomCode);
//
//    @Query(value = "SELECT * FROM Participant WHERE username = :username", nativeQuery = true)
//    List<Room> getRoomsPresent(@Param("username") String username);
//}
//
