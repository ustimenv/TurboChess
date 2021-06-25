package turbochess.service.room;

import turbochess.model.room.Room;
import turbochess.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.text.MessageFormat.format;

@Service
public class RoomServiceImp implements RoomService{

    @Autowired
    private RoomRepository repository;

    @Override
    public Room createRoom(String code, int capacity) throws RoomException {
        if(roomExists(code)){
            throw new RoomException(format("Room {0} already exists!", code));
        }
        if(capacity < 2){
            throw new RoomException(format("Can''t create a room with {0} players, must be at least 2!", capacity));
        }
        return new Room(code, capacity);
    }

    @Override
    public boolean roomExists(String roomCode){
        return roomCode != null && repository.countByCode(roomCode) > 0;
    }


    @Override
    public Room getRoomByCode(String roomCode) throws RoomException{
        if(roomExists(roomCode)){
            return repository.getRoomByCode(roomCode);
        } else  throw new RoomException(format("Room {0} doesn''t exist!", roomCode));
    }

}
