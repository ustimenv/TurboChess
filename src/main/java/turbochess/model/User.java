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
public class User{
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

	@ManyToMany
	@JoinTable(name="friends",
			joinColumns=@JoinColumn(name = "subject_id"),
			inverseJoinColumns=@JoinColumn(name = "friend_id"))
	List<User> friends;

	/**
	 * Checks whether this user has a given role.
	 * @param role to check
	 * @return true iff this user has that role.
	 */
	public boolean hasRole(Role role) {
		String roleName = role.name();
		return Arrays.asList(roles.split(",")).contains(roleName);
	}

	public boolean isPasswordValid(){
		return password != null && !password.isEmpty() && password.equals(passwordConfirm);
	}

	// somewhat based on https://en.wikipedia.org/wiki/Chess_rating_system
	public void updateScoreOnVictory(User loser){
		int scoreIncrease = (this.getElo() + loser.getElo()) / 20;
		setElo(getElo() + Math.min(scoreIncrease, 100));
	}

	public void updateScoreOnDraw(User other){
		int scoreIncrease = (this.getElo() + other.getElo()) / 100;
		setElo(getElo() + Math.min(scoreIncrease, 20));
	}

	public void updateScoreOnDefeat(User winner){
		int scoreDecrease = (this.getElo() + winner.getElo()) / 60;
		setElo(getElo() - Math.min(scoreDecrease, 30));
	}
}
