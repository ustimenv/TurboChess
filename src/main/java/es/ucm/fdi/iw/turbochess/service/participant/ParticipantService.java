package es.ucm.fdi.iw.turbochess.service.participant;

import es.ucm.fdi.iw.turbochess.model.room.Participant;
import es.ucm.fdi.iw.turbochess.model.room.ParticipantKey;
import es.ucm.fdi.iw.turbochess.model.room.Room;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ParticipantService{
    // ParticipantKey contains participant's username and the room in which they are participating
    List <Room> getRoomsPresent(String username)                    throws ParticipantException;
    void deleteParticipant(ParticipantKey pk)                       throws ParticipantException;
    List <Participant> getRoomParticipants(String roomCode)         throws ParticipantException;
    boolean isParticipantInRoom(ParticipantKey pk)                  throws ParticipantException;

    Participant getParticipant(ParticipantKey pk)                   throws ParticipantException;
}
