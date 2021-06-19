package es.ucm.fdi.iw.turbochess.service.room;

import es.ucm.fdi.iw.turbochess.model.User;
import es.ucm.fdi.iw.turbochess.model.room.Participant;
import es.ucm.fdi.iw.turbochess.model.room.Room;
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
