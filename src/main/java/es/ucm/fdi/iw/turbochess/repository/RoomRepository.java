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
        List <Room> getRoomsByCode(@Param("code") String roomCode);     // 1-element list since room codes are unique

        @Query("SELECT * FROM Room  WHERE r.code = :code")
        List <Participant> getRoomParticipantsByCode(String roomCode);

        @Query(value = "SELECT COUNT(r) FROM Room r WHERE r.code = :code")
        int countByCode(@Param("code") String roomCode);

        @Modifying
        @Query("DELETE * FROM Room r WHERE r.num_participants < 1")
        void deleteAllEmptyRooms();

        @Modifying
        @Query("DELETE * FROM Room r WHERE r.code = :code")
        void deleteRoomByCode(@Param("code") String code);
}

