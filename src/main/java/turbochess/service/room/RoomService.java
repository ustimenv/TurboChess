package turbochess.service.room;

import turbochess.model.room.Participant;
import turbochess.model.room.Room;
import org.springframework.stereotype.Service;

@Service
public interface RoomService {

    Room createRoom(String roomCode, int capacity)                  throws RoomException;
    Participant.Role assignRole(String roomCode, Participant p)     throws RoomException;
    void joinRoom(String roomCode, Participant p)                   throws RoomException;
    void leaveRoom(String roomCode, Participant p)                  throws RoomException;

    Room getRoomByCode(String roomCode)                             throws RoomException;
    boolean roomExists(String roomCode);


}
