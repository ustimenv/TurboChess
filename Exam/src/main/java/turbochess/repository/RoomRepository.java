package turbochess.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import turbochess.model.room.Room;

import java.util.List;

public interface RoomRepository extends CrudRepository<Room, String>{
	@Query(value = "SELECT * FROM Room WHERE code = :code", nativeQuery = true)
	Room getRoomByCode(@Param("code") String code);

	@Query(value = "SELECT COUNT(*) FROM Room WHERE code = :code", nativeQuery = true)
	int countByCode(@Param("code") String code);

	@Query(value = "SELECT * FROM Room WHERE num_participants <= capacity LIMIT :maxRooms", nativeQuery = true)
	List<Room> getRoomsBelowCapacity(@Param("maxRooms") int maxRooms);
}

