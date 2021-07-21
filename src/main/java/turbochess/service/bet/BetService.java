package turbochess.service.bet;

import org.springframework.stereotype.Service;
import turbochess.model.room.Bet;
import turbochess.model.room.Game;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;

import java.util.List;

@Service
public interface BetService{
    List <Bet> getParticipantBets(Participant p)    throws BetException;
    int getParticipantTotalBet(Participant p)       throws BetException;
    void deleteRoomBets(Room room)                  throws BetException;
    List<Bet> getRoomBetsByResult(String roomCode, Game.Result result);

    Bet save(Bet bet);
}
