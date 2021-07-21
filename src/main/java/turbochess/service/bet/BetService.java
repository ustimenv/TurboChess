package turbochess.service.bet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import turbochess.model.room.Bet;
import turbochess.model.room.Game;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;

import java.util.List;

@Service
public class BetService{

    @Autowired
    private BetRepository repository;

    public Bet save(Bet bet){
        return repository.save(bet);
    }

    public void delete(Bet bet){
        repository.delete(bet);
    }

    public List<Bet> getParticipantBets(Participant p){
        return repository.getParticipantBets(p.getId());
    }



    public int getParticipantTotalBet(Participant p){
        return getParticipantBets(p).stream().mapToInt(Bet::getAmount).sum();
    }

    public void deleteRoomBets(Room room){
        repository.deleteRoomBets(room.getCode());
    }

    public List<Bet> getRoomBetsByResult(String roomCode, Game.Result result){
        return repository.getRoomBetsByResult(roomCode, result);
    }

}
