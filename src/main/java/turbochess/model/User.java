package turbochess.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import turbochess.model.room.Participant;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Data
@NoArgsConstructor

//@NamedQueries({
//		@NamedQuery(name = "User.search_result",
//				query = "SELECT u FROM User u WHERE u.username LIKE :username AND u.enabled = 1"),
//
//		@NamedQuery(name = "User.byUsername",
//				query = "SELECT u FROM User u WHERE u.username = :username AND u.enabled = 1"),
//
//		@NamedQuery(name = "User.hasUsername",
//				query = "SELECT COUNT(u) FROM User u WHERE u.username = :username"),
//
//		@NamedQuery(name = "User.byId",
//				query = "SELECT u FROM User u WHERE u.id = :id"),
//
//		@NamedQuery(name = "User.findAll",
//				query = "SELECT u FROM User u "),
//
//		@NamedQuery(name = "User.getBalanceByUsername",
//				query = "SELECT u.coins FROM User u WHERE u.username = :username")
//})
//@NamedNativeQueries({
//		@NamedNativeQuery(name = "User.friends", query = "SELECT * FROM user_friends " +
//		"LEFT JOIN user on user_friends.friends_id =user.id WHERE user_id= :userid " +
//		"UNION ALL" +
//		" SELECT  * FROM user_friends LEFT JOIN user on user_friends.user_id=user.id WHERE friends_id= :userid", resultClass = User.class),
//		@NamedNativeQuery(name="User.byUsernameNative",
//						  query= "SELECT * FROM User WHERE username LIKE :username AND enabled = 1", resultClass=User.class),
//
//
//})
public class User{//} implements Transferable<User.Transfer> {
	private static Logger log = LogManager.getLogger(User.class);

	public enum Role {
		USER,			// used for logged-in, non-privileged users
		ADMIN,			// used for maximum privileged users
		MODERATOR,		// between user and admin privilege-wise
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	/** username for login purposes; must be unique */
	@Column(nullable = false, unique = true)
	private String username;
	/** encoded password; use setPassword(SecurityConfig.encode(plaintextPassword)) to encode it  */
	@Column(nullable = false)
	private String password;
	@Transient
	private String passwordConfirm;
	@Column(nullable = false)
	private String roles; // split by ',' to separate roles
	private byte enabled;

	@Column(name="elo", nullable=false)
	private int elo;
	@Column(name="matches_won", nullable=false)
	private int matches_won;
	@Column(name="matches_played", nullable=false)
	private int matches_played;
	@Column(name="matches_lost", nullable=false)
	private int matches_lost;

	@Column(name="coins", nullable=false)	// coins can be gained by betting as an observer or playing matches
	private int coins;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)		// user can participate in any number of rooms simultaneously,
	private List<Participant> participations = new ArrayList<>();	// provided they don't double-join any

//	@OneToMany
//	@JoinColumn(name = "sender_id")
//	private List<Message> sent = new ArrayList<>();
//
//	@OneToMany
//	@JoinColumn(name = "recipient_id")
//	private List<Message> received = new ArrayList<>();

	@ManyToMany
	@JoinTable(name="friends",
			joinColumns=@JoinColumn(name = "subject_id"),
			inverseJoinColumns=@JoinColumn(name = "friend_id"))
	List<User> friends;
	// utility methods

	/**
	 * Checks whether this user has a given role.
	 * @param role to check
	 * @return true iff this user has that role.
	 */
	public boolean hasRole(Role role) {
		String roleName = role.name();
		return Arrays.asList(roles.split(",")).contains(roleName);
	}

    @Getter
    @AllArgsConstructor
    public static class Transfer {
		private long id;
        private String username;
		private int totalReceived;
		private int totalSent;
    }

//	@Override
//    public Transfer toTransfer() {
//		return new Transfer(id,	username, received.size(), sent.size());
//	}
//	@Override
//    public Transfer toTransfer() {
//		return new Transfer(id,	username, 0, 0);
//	}

//	@Override
//	public String toString() {
//		return toTransfer().toString();
//	}

	public boolean isPasswordValid(){
		return password != null && !password.isEmpty() && password.equals(passwordConfirm);
	}

	public void updateScoreOnVictory(User loser){ // somewhat based on https://en.wikipedia.org/wiki/Chess_rating_system
		int eloDelta = loser.getElo() - this.getElo();
		int scoreIncrease = 0;
		int baseScoreGain = 100;
		int maxScoreGain=150;
		if(eloDelta > 0){	// won against a stronger opponent
			scoreIncrease = (eloDelta / this.getElo()) * baseScoreGain;
		} else{				// won against a weaker opponent
			scoreIncrease = (-eloDelta / loser.getElo()) * baseScoreGain;
		}
		scoreIncrease = Math.min(scoreIncrease, maxScoreGain);
		setElo(getElo() + scoreIncrease);
	}
}
