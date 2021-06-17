package es.ucm.fdi.iw.turbochess.repository;

import es.ucm.fdi.iw.turbochess.model.Participant;
import es.ucm.fdi.iw.turbochess.model.Room;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepository extends CrudRepository<Room, String>{
        @Query("SELECT r FROM Room r WHERE r.code = :code")
        Room getRoomByCode(@Param("code") String code);     // 1-element list since room codes are unique

        @Query(value = "SELECT Participant.* FROM Participant LEFT JOIN Room on Participant.code = Room.code WHERE r.code = :code", nativeQuery = true)
        List <Participant> getParticipantByCode(String code);

        @Query(value = "SELECT COUNT(r) FROM Room r WHERE r.code = :code")
        int countByCode(@Param("code") String code);

        @Modifying
        @Query(value = "DELETE * FROM Room WHERE Room.num_participants < 1", nativeQuery = true)
        void deleteAllEmptyRooms();

        @Modifying
        @Query(value = "DELETE * FROM Room WHERE Room.code = :code", nativeQuery = true)
        void deleteRoomByCode(@Param("code") String code);
}

