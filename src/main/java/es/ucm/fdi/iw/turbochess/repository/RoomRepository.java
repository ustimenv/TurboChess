package es.ucm.fdi.iw.turbochess.repository;

import es.ucm.fdi.iw.turbochess.model.room.Participant;
import es.ucm.fdi.iw.turbochess.model.room.Room;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepository extends CrudRepository<Room, String>{
	@Query("SELECT r FROM Room r WHERE r.code = :code")
	Room getRoomByCode(@Param("code") String code);

//	@Query(value = "SELECT p FROM Participant p WHERE p.room_code = :code")
//	List <Participant> getRoomParticipants(String code);

	@Query(value = "SELECT COUNT(r) FROM Room r WHERE r.code = :code")
	int countByCode(@Param("code") String code);

	@Modifying
	@Query(value = "DELETE FROM Room WHERE Room.num_participants < 1", nativeQuery = true)
	void deleteAllEmptyRooms();

	@Modifying
	@Query(value = "DELETE FROM Room WHERE Room.code = :code", nativeQuery = true)
	void deleteRoomByCode(@Param("code") String code);


}

