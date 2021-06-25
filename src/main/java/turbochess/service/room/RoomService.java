package turbochess.service.room;

import org.springframework.stereotype.Service;
import turbochess.model.room.Room;

@Service
public interface RoomService {

    Room createRoom(String roomCode, int capacity)                                      throws RoomException;
    Room getRoomByCode(String roomCode)                                                 throws RoomException;
    boolean roomExists(String roomCode);

}
