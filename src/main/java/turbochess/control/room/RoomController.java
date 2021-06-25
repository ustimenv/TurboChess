package turbochess.control.room;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import turbochess.model.User;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;
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
	protected EntityManager entityManager;

	// QUERY UTILITIES

	protected User getUserByUsername(String username) throws RoomException{
		User u = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username AND enabled = 1", User.class)
				.setParameter("username", username)
				.getSingleResult();
		//User u = entityManager.createQuery("User.byUsername", User.class)	//todo why? {java.lang.IllegalArgumentException: Could not locate named parameter [username], expecting one of []}
		//								.setParameter("username", username)
		//								.getSingleResult();
		if(u != null){
			return u;
		} else	throw new RoomException(format("Unable to find user {0}", username));
	}

	protected Participant getParticipantByUsernameAndRoom(User user, Room room) throws RoomException{
		String username, roomCode;
		try{
			username = user.getUsername();
			roomCode = room.getCode();
			Participant p = entityManager.createNamedQuery("Participant.getByUserIdAndRoomCode", Participant.class)
					.setParameter("user_id", user.getId())
					.setParameter("code", room.getCode())
					.getSingleResult();
			if(p != null){
				return p;
			} else	throw new RoomException(format("Unable to find participant {0} in room {1}", username, roomCode));
		} catch(Exception e){
			throw new RoomException(e.getMessage());
		}
	}
	@SuppressWarnings("unchecked")
	protected List<Participant> getRoomParticipants(Room room){
		return entityManager.createNamedQuery("Participant.getRoomParticipants")
				.setParameter("code", room.getCode())
				.getResultList();

	}
	protected Participant.Role getParticipantRole(User user, Room room){
		return (Participant.Role) entityManager.createNamedQuery("Participant.getRoleByUserIdAndRoomCode")
				.setParameter("user_id", user.getId())
				.setParameter("code", room.getCode())
				.getSingleResult();
	}
	protected void increaseParticipantBetBy(User user, Room room, int betAmount){
		entityManager.createNamedQuery("Participant.increaseBetAmountBy")
				.setParameter("user_id", user.getId())
				.setParameter("code", room.getCode())
				.setParameter("betAmount", betAmount)
				.executeUpdate();
	}
	protected void removeUserCoins(User user, int amount){
		entityManager.createNamedQuery("User.removeCoins")
				.setParameter("username", user.getUsername())
				.setParameter("amount", amount)
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