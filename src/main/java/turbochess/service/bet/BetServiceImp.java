package turbochess.service.bet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import turbochess.model.chess.Bet;
import turbochess.model.chess.Game;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;
import turbochess.repository.BetRepository;

import java.util.List;

@Service
public class BetServiceImp implements BetService{

    @Autowired
    private BetRepository repository;

    @Override
    public List<Bet> getParticipantBets(Participant p) throws BetException{
        return repository.getParticipantBets(p.getId());
    }

    @Override
    public int getParticipantTotalBet(Participant p) throws BetException{
        return getParticipantBets(p).stream().mapToInt(Bet::getValue).sum();
    }

    @Override
    public void deleteRoomBets(Room room) throws BetException{
        repository.deleteRoomBets(room.getCode());
    }

    @Override
    public List<Bet> getRoomBetsByResult(String roomCode, Game.Result result){
        return repository.getRoomBetsByResult(roomCode, result);
    }
}
