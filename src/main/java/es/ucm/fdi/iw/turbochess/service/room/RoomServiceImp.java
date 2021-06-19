package es.ucm.fdi.iw.turbochess.service.room;

import es.ucm.fdi.iw.turbochess.model.User;
import es.ucm.fdi.iw.turbochess.model.room.Participant;
import es.ucm.fdi.iw.turbochess.model.room.Room;
import es.ucm.fdi.iw.turbochess.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.text.MessageFormat.format;

@Service
public class RoomServiceImp implements RoomService{

    @Autowired
    private RoomRepository roomRepository;


    @Override
//    @Transactional
    public Room createRoom(String code, int capacity) throws RoomException {
        if(roomExists(code)){
            throw new RoomException(format("Room {0} already exists!", code));
        }
        if(capacity < 2){
            throw new RoomException(format("Can''t create a room with {0} players, must be at least 2!", capacity));
        }
        Room room = new Room(code, capacity);
        roomRepository.save(room);
        return room;
    }

    @Override
    public Participant.Role assignRole(String roomCode, Participant p) throws RoomException{
        if(roomExists(roomCode)){
            Room room = getRoomByCode(roomCode);
            return room.assignRole(p);
        } else throw new RoomException(format("Can't assign a role in room {0} since it doesn't exist", roomCode));
    }

    @Override
//    @Transactional
    public void joinRoom(String roomCode, Participant p) throws RoomException{
        if(roomExists(roomCode)){
            Room room = getRoomByCode(roomCode);
            room.addParticipant(p);
            roomRepository.save(room);
        } else throw new RoomException(format("User {0} is attempting to join a non-existent room {1}!", p.getUser().getUsername(), roomCode));

    }

    @Override
    public void leaveRoom(String roomCode, Participant p) throws RoomException{
        Room room = getRoomByCode(roomCode);
        room.removeParticipant(p);

        if(room.getNumParticipants() < 1){
            roomRepository.delete(room);
        } else  roomRepository.save(room);
    }

    @Override
    public boolean roomExists(String roomCode){
        return roomCode != null && roomRepository.countByCode(roomCode) > 0;
    }


    @Override
    public Room getRoomByCode(String roomCode) throws RoomException{
        if(roomExists(roomCode)){
            return roomRepository.getRoomByCode(roomCode);
        } else  throw new RoomException(format("Room {0} doesn''t exist!", roomCode));
    }
}
