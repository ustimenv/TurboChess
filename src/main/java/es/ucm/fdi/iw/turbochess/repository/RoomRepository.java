package es.ucm.fdi.iw.turbochess.repository;

import es.ucm.fdi.iw.turbochess.model.room.Room;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends CrudRepository<Room, String>{
	@Query(value = "SELECT * FROM Room WHERE code = :code", nativeQuery = true)
	Room getRoomByCode(@Param("code") String code);

	@Query(value = "SELECT COUNT(*) FROM Room WHERE code = :code", nativeQuery = true)
	int countByCode(@Param("code") String code);

	@Modifying
	@Query(value = "DELETE FROM Room WHERE Room.num_participants < 1", nativeQuery = true)
	void deleteAllEmptyRooms();

	@Modifying
	@Query(value = "DELETE FROM Room WHERE Room.code = :code", nativeQuery = true)
	void deleteRoomByCode(@Param("code") String code);


}

