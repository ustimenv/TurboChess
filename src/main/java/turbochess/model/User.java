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


/**
 * A user; can be an Admin, a User, or a Moderator
 *
 * Users can log in and send each other messages.
 *
 * @author mfreire
 */
/**
 * An authorized user of the system.
 */
@Entity
@Data
@NoArgsConstructor

@NamedQueries({
	@NamedQuery(name="User.search_result",
				query= "SELECT u FROM User u WHERE u.username LIKE :username AND u.enabled = 1"),

		@NamedQuery(name="User.byUsername",
					query= "SELECT u FROM User u WHERE u.username = :username AND u.enabled = 1"),

		@NamedQuery(name="User.hasUsername",
                	query= "SELECT COUNT(u) FROM User u WHERE u.username = :username"),

		@NamedQuery(name="User.byId",
					query= "SELECT u FROM User u WHERE u.id = :id"),

		@NamedQuery(name="User.findAll",
					query= "SELECT u FROM User u "),

		@NamedQuery(name="User.getBalanceByUsername",
					query="SELECT u.coins FROM User u WHERE u.username = :username"),

		@NamedQuery(name="User.addCoins",
					query="UPDATE User u SET u.coins = u.coins + :amount WHERE u.username = :username"),

		@NamedQuery(name="User.removeCoins",
					query="UPDATE User u SET u.coins = u.coins - :amount WHERE u.username = :username")
		}
)
@NamedNativeQueries({
		@NamedNativeQuery(name = "User.friends", query = "SELECT * FROM user_friends " +
		"LEFT JOIN user on user_friends.friends_id =user.id WHERE user_id= :userid " +
		"UNION ALL" +
		" SELECT  * FROM user_friends LEFT JOIN user on user_friends.user_id=user.id WHERE friends_id= :userid", resultClass = User.class),
		@NamedNativeQuery(name="User.byUsernameNative",
						  query= "SELECT * FROM User WHERE username LIKE :username AND enabled = 1", resultClass=User.class),


})
public class User implements Transferable<User.Transfer> {



	private static Logger log = LogManager.getLogger(User.class);	

	public enum Role {
		USER,			// used for logged-in, non-priviledged users
		ADMIN,			// used for maximum priviledged users
		
		MODERATOR,		// remove or add roles as needed
	}
	
	// do not change these fields

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
	@Override
    public Transfer toTransfer() {
		return new Transfer(id,	username, 0, 0);
	}

	@Override
	public String toString() {
		return toTransfer().toString();
	}

	public boolean samePasword(){
		return this.password.equals(this.passwordConfirm);
	}

	public void updateScoreOnVictory(User loser){
		int scoreDelta = loser.getElo()-getElo();
		int K = 32;
		int C=200;//TODO finish this https://en.wikipedia.org/wiki/Chess_rating_system#Example
//		setElo(getElo() + K/2 * );
	}
}
