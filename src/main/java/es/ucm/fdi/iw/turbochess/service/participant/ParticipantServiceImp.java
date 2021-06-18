package es.ucm.fdi.iw.turbochess.service.participant;

import es.ucm.fdi.iw.turbochess.model.room.Participant;
import es.ucm.fdi.iw.turbochess.model.room.ParticipantKey;
import es.ucm.fdi.iw.turbochess.model.room.Room;
import es.ucm.fdi.iw.turbochess.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParticipantServiceImp implements ParticipantService{

    @Autowired
    private ParticipantRepository participantRepository;

    @Override
    @Transactional
    public List<Room> getRoomsPresent(String username) throws ParticipantException{
        if(username == null){
            throw new ParticipantException("[getRoomsPresent]: arg is null");
        } else{
            return participantRepository.getRoomsPresent(username);
        }
    }

    @Override
    @Transactional
    public void deleteParticipant(ParticipantKey pk) throws ParticipantException{
        if(pk == null || pk.getRoomCode() == null || pk.getUsername() == null){
            throw new ParticipantException("[deleteParticipant]: arg is null");
        } else{
            participantRepository.deleteParticipant(pk.getRoomCode(), pk.getUsername());
        }
    }

    @Override
    public List<Participant> getRoomParticipants(String roomCode) throws ParticipantException{
        if(roomCode == null){
            throw new ParticipantException("[getRoomParticipants]: arg is null");
        } else{
            return participantRepository.getRoomParticipants(roomCode);
        }
    }

    @Override
    public boolean isParticipantInRoom(ParticipantKey pk) throws ParticipantException{
        if(pk == null || pk.getRoomCode() == null || pk.getUsername() == null){
            throw new ParticipantException("[isParticipantInRoom]: arg is null");
        } else{
            return (participantRepository.countParticipants(pk.getRoomCode(), pk.getUsername()) > 0);
        }
    }

    @Override
    @Transactional
    public Participant getParticipant(ParticipantKey pk) throws ParticipantException{
        if(pk == null || pk.getRoomCode() == null || pk.getUsername() == null){
            throw new ParticipantException("[isParticipantInRoom]: arg is null");
        } else{
            return participantRepository.getParticipant(pk.getRoomCode(), pk.getUsername());
        }
    }

}
