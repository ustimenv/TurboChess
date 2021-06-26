package turbochess.control.room;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import turbochess.model.User;
import turbochess.service.bet.BetService;
import turbochess.service.participant.ParticipantService;
import turbochess.service.room.RoomException;
import turbochess.service.room.RoomService;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static java.text.MessageFormat.format;

@Controller
public class RoomController{

	@Autowired
	protected SimpMessagingTemplate template;

	@Autowired
	protected RoomService roomService;

	@Autowired
	protected ParticipantService participantService;

	@Autowired
	protected BetService betService;

	@Autowired
	protected EntityManager entityManager;

	// QUERY UTILITIES

	protected User getUserByUsername(String username) throws RoomException{
		User u = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username AND enabled = 1", User.class)
								.setParameter("username", username)
								.getSingleResult();
		if(u != null){
			return u;
		} else	throw new RoomException(format("Unable to find user {0}", username));
	}

	protected User getUserByID(long id) throws RoomException{
		User u = entityManager.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class)
								.setParameter("id", id)
								.getSingleResult();
		if(u != null){
			return u;
		} else	throw new RoomException(format("Unable to find user with id {0}", id));
	}

	protected void setUserCoins(long userId, int newCoins) throws RoomException{
		entityManager.createQuery("UPDATE User u SET u.coins = :newCoins WHERE u.id = :userID")
								.setParameter("newCoins", newCoins)
								.setParameter("userID", userId)
								.executeUpdate();
	}


	// CODE CREATION
	private int n1=0, n2=0, n3=0;								// counters for the 3 segments that form the room code
	private char[] letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	private List<String> segments = new ArrayList<>();
	private int len;

	@PostConstruct
	private void initialiseCodeSegments(){
		for(char c1 : letters){
			for(char c2 : letters){
				segments.add(String.valueOf(c1) + c2);
			}
		}
		len=segments.size();
	}

	protected synchronized String nextRoomCode(){
		String code=segments.get(n1) + segments.get(n2) + segments.get(n3);
		if(n3-1>len){
			if(n2-1>len){
				if(n1-1>len){
					n1=0;
				} else n1++;
				n2=0;
			} else n2++;
			n3=0;
		} else n3++;
		return code;
	}
}