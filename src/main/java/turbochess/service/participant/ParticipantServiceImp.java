package turbochess.service.participant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import turbochess.model.User;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;
import turbochess.repository.ParticipantRepository;
import turbochess.repository.RoomRepository;
import turbochess.service.room.RoomException;

import java.util.List;

import static java.text.MessageFormat.format;

@Service
public class ParticipantServiceImp implements ParticipantService{

    @Autowired
    private ParticipantRepository repository;


    @Override
    public Participant getParticipantByUsernameAndRoom(Room room, User user) throws ParticipantException{
        String username = user.getUsername();
        String roomCode = room.getCode();
        Participant p = repository.getParticipant(roomCode, user.getId());

        if(p != null){
            return p;
        } else	throw new ParticipantException(format("Unable to find participant {0} in room {1}", username, roomCode));
    }

    @Override
    public List<Participant> getRoomParticipants(Room room){
        return repository.getRoomParticipants(room.getCode());

    }
}
