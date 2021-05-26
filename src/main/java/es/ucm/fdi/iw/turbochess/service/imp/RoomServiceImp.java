package es.ucm.fdi.iw.turbochess.service.imp;

import es.ucm.fdi.iw.turbochess.model.Participant;
import es.ucm.fdi.iw.turbochess.model.Room;
import es.ucm.fdi.iw.turbochess.repository.RoomRepository;
import es.ucm.fdi.iw.turbochess.service.RoomException;
import es.ucm.fdi.iw.turbochess.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class RoomServiceImp implements RoomService{

    @PersistenceContext
    private EntityManager entityManager;        //hmm

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public Room createRoom(String roomCode, int capacity) throws RoomException {
        if(roomExists(roomCode)){
            throw new RoomException("Room "+ roomCode + " already exists!");
        }
        if(capacity < 2){
            throw new RoomException("Can't create a room with "+ capacity + " players, must be at least 2!");
        }
        Room room = new Room(roomCode, capacity);
        roomRepository.save(room);
        return room;
    }

    @Override
    public void joinRoom(String roomCode, Participant p) throws RoomException{
        if(roomExists(roomCode)){
            Room room = getRoomByCode(roomCode);
            if(room.addParticipant(p)){
                roomRepository.save(room);
            }
            throw new RoomException("Capacity exceeded for room " + roomCode +"!");
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
                                    " room "+roomCode +" because they are not in the room!");
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
            return roomRepository.getRoomsByCode(roomCode).get(0);
        }
        throw new RoomException("Room " + roomCode + " doesn't exist!");
    }
}
