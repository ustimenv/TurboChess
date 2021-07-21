package turbochess.service.room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;

import java.util.List;

import static java.text.MessageFormat.format;

@Service
public class RoomService {
    @Autowired
    private RoomRepository repository;

    public Room save(Room room){
        return repository.save(room);
    }

    public void delete(Room room){
        repository.delete(room);
    }

    public Room createRoom(String code, int capacity) throws RoomException {
        if(roomExists(code)){
            throw new RoomException(format("Room {0} already exists!", code));
        }
        if(capacity < 2){
            throw new RoomException(format("Can''t create a room with {0} players, must be at least 2!", capacity));
        }
        return new Room(code, capacity);
    }

    public boolean roomExists(String roomCode){
        return roomCode != null && repository.countByCode(roomCode) > 0;
    }


    public Room getRoomByCode(String roomCode) throws RoomException{
        if(roomExists(roomCode)){
            return repository.getRoomByCode(roomCode);
        } else  throw new RoomException(format("Room {0} doesn''t exist!", roomCode));
    }

    public List<Room> getAvailableRooms(int maxRooms){
        return repository.getRoomsBelowCapacity(maxRooms);
    }
}
