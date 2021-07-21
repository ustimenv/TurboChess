package turbochess.service.room;

import org.springframework.stereotype.Service;
import turbochess.model.room.Room;

import java.util.List;

@Service
public interface RoomService {

    Room createRoom(String roomCode, int capacity)  throws RoomException;
    Room getRoomByCode(String roomCode)             throws RoomException;
    List<Room> getAvailableRooms(int maxRooms);
    boolean roomExists(String roomCode);

    Room save(Room room);

}
