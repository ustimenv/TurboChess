package es.ucm.fdi.iw.turbochess.service.room;

import es.ucm.fdi.iw.turbochess.model.room.Participant;
import es.ucm.fdi.iw.turbochess.model.room.Room;
import es.ucm.fdi.iw.turbochess.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomServiceImp implements RoomService{

    @Autowired
    private RoomRepository roomRepository;


    @Override
    @Transactional
    public void createRoom(String code, int capacity) throws RoomException {
        if(roomExists(code)){       // really shouldnt happen but doesn't hurt to check twice
            throw new RoomException("Room "+ code + " already exists!");
        }
        if(capacity < 2){
            throw new RoomException("Can't create a room with " + capacity + " players, must be at least 2!");
        }
        Room r = new Room(code, capacity);
        roomRepository.save(r);
    }

    @Override
    @Transactional
    public void joinRoom(String roomCode, Participant p) throws RoomException{
        System.out.println();
        if(roomExists(roomCode)){
            Room room = getRoomByCode(roomCode);
            if(room.addParticipant(p)){
                roomRepository.save(room);
            } else{
                throw new RoomException("Capacity exceeded for room " + roomCode +"!");
            }
        } else {
            throw new RoomException("Participant " + p.getUser().getUsername() + " is attempting to join a non-existent" +
                    " room "+roomCode +"!");
        }
    }

    @Override
    public void leaveRoom(String roomCode, Participant p) throws RoomException{
        Room room = getRoomByCode(roomCode);
        if(!room.removeParticipant(p)){
            throw new RoomException("Participant " + p.getUser().getUsername() + " can't leave " +
                                    " room "+roomCode + " because they are not in the room!");
        }

        if(room.getNumParticipants() < 1){
            roomRepository.deleteRoomByCode(roomCode);
        } else{
            roomRepository.save(room);
        }
    }

    @Override
    public boolean roomExists(String roomCode){
        return roomCode != null && roomRepository.countByCode(roomCode) > 0;
    }


    @Override
    public void deleteAllEmptyRooms(){              // shouldn't be necessary of leaveRoom works as intended
        roomRepository.deleteAllEmptyRooms();
    }

    @Override
    public Room getRoomByCode(String roomCode) throws RoomException{
        if(roomExists(roomCode)){
            return roomRepository.getRoomByCode(roomCode);
        }
        throw new RoomException("Room " + roomCode + " doesn't exist!");
    }

}
