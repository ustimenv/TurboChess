package es.ucm.fdi.iw.turbochess.model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.*;
import javax.swing.ImageIcon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import es.ucm.fdi.iw.model.Transferable;


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
	query="SELECT u FROM User u "
			+ "WHERE u.username like :username AND u.enabled = 1"),

		@NamedQuery(name="User.byUsername",
                query="SELECT u FROM User u "
                        + "WHERE u.username = :username AND u.enabled = 1"),
        @NamedQuery(name="User.hasUsername",
                query="SELECT COUNT(u) "
                        + "FROM User u "
                        + "WHERE u.username = :username"),
		})
@NamedNativeQueries({
		@NamedNativeQuery(name = "User.friends", query = "SELECT * FROM user_friends " +
		"LEFT JOIN user on user_friends.friends_id =user.id WHERE user_id= :userid " +
		"UNION ALL" +
		" SELECT  * FROM user_friends LEFT JOIN user on user_friends.user_id=user.id WHERE friends_id= :userid",resultClass = User.class)
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

	@OneToMany
	@JoinColumn(name = "sender_id")
	private List<Message> sent = new ArrayList<>();
	@OneToMany
	@JoinColumn(name = "recipient_id")	
	private List<Message> received = new ArrayList<>();

	@OneToMany
	List<User> friendshipRequests;

	@ManyToMany
	List<User> friends;
	// utility methods
	
	/**
	 * Checks whether this user has a given role.
	 * @param role to check
	 * @return true iff this user has that role.
	 */
	public boolean hasRole(Role role) {
		String roleName = role.name();
		return Arrays.stream(roles.split(","))
				.anyMatch(r -> r.equals(roleName));
	}

    @Getter
    @AllArgsConstructor
    public static class Transfer {
		private long id;
        private String username;
		private int totalReceived;
		private int totalSent;
    }

	@Override
    public Transfer toTransfer() {
		return new Transfer(id,	username, received.size(), sent.size());
	}
	
	@Override
	public String toString() {
		return toTransfer().toString();
	}

	public boolean samePasword(){
		return (this.password == this.passwordConfirm);
	}
}
