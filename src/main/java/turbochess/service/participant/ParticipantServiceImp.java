package turbochess.service.participant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import turbochess.model.User;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;
import turbochess.repository.ParticipantRepository;

import java.util.List;

import static java.text.MessageFormat.format;

@Service
public class ParticipantServiceImp implements ParticipantService{

    @Autowired
    private ParticipantRepository repository;

    @Override
    public boolean isUserInRoom(Room room, User user){
        return 0 < repository.countParticipants(room.getCode(), user.getId());
    }

    @Override
    public Participant createParticipant(Room room, User user) throws ParticipantException{
        if(isUserInRoom(room, user)){
            throw new ParticipantException(format("User {0} is already in room {1}", user.getUsername(), room.getCode()));
        } else{
            return new Participant(room, user);
        }
    }

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
    public Participant getParticipantBySessionId(String sessionId) throws ParticipantException{
        Participant p = repository.getBySessionId(sessionId);
        if(p != null){
            return p;
        } else	throw new ParticipantException(format("Unable to find participant wish session id={0}", sessionId));
    }

    @Override
    public List<Participant> getRoomParticipants(Room room){
        return repository.getRoomParticipants(room.getCode());
    }

    @Override
    public List<Long> getUserIdsInRoomWithRole(String roomCode, Participant.Role role) throws ParticipantException{
        return repository.getUserIdsInRoomWithRole(roomCode, role);
    }

    @Override
    public Participant save(Participant participant){
        return repository.save(participant);
    }

    @Override
    public Participant getUnsubscribedParticipantInstance(User user) throws ParticipantException{
        return repository.getUnsubscribedParticipantWithName(user.getUsername());
    }
}
