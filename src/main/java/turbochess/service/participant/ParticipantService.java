package turbochess.service.participant;

import org.springframework.stereotype.Service;
import turbochess.model.User;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;

import java.util.List;

@Service
public interface ParticipantService{

    Participant getParticipantByUsernameAndRoom(Room room, User user) throws ParticipantException;
    List<Participant> getRoomParticipants(Room room);

}
