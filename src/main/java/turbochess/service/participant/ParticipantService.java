package turbochess.service.participant;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import turbochess.model.User;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;

import java.util.List;

@Service
public interface ParticipantService{

    boolean isUserInRoom(Room room, User user);
    Participant createParticipant(Room room, User user)               throws ParticipantException;
    Participant getParticipantByUsernameAndRoom(Room room, User user) throws ParticipantException;
    Participant getParticipantBySessionId(String sessionId)           throws ParticipantException;


    List<Participant> getRoomParticipants(Room room);

    List<Long> getUserIdsInRoomWithRole(String roomCode, Participant.Role role) throws ParticipantException;

    Participant save(Participant participant);
    Participant getUnsubscribedParticipantInstance(User user)       throws ParticipantException;
}
