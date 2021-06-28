package turbochess.repository;

import turbochess.model.room.Room;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepository extends CrudRepository<Room, String>{
	@Query(value = "SELECT * FROM Room WHERE code = :code", nativeQuery = true)
	Room getRoomByCode(@Param("code") String code);

	@Query(value = "SELECT COUNT(*) FROM Room WHERE code = :code", nativeQuery = true)
	int countByCode(@Param("code") String code);

	@Query(value = "SELECT num_participants FROM Room WHERE code = :code", nativeQuery = true)
	int getNumParticipants(@Param("code") String code);

	@Query(value = "SELECT capacity FROM Room WHERE code = :code", nativeQuery = true)
	int getCapacity(@Param("code") String code);

	@Modifying
	@Query(value = "DELETE FROM Room WHERE num_participants < 1", nativeQuery = true)
	void deleteAllEmptyRooms();


	@Query(value = "SELECT * FROM Room WHERE num_participants < capacity LIMIT :maxRooms", nativeQuery = true)
	List<Room> getRoomsBelowCapacity(@Param("maxRooms") int maxRooms);

	@Query(value = "SELECT * FROM Room", nativeQuery = true)
	List<Room> getAllRooms();

	@Modifying
	@Query(value = "DELETE FROM Room WHERE Room.code = :code", nativeQuery = true)
	void deleteRoomByCode(@Param("code") String code);


}

