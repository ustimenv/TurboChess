package es.ucm.fdi.iw.turbochess.model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
public class User implements Transferable<User.Transfer> {
	private static Logger log = LogManager.getLogger(User.class);	

	public enum Role {
		USER,			// used for logged-in, non-priviledged users
		ADMIN,			// used for maximum priviledged users
	}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(targetEntity = User.class)
    private List<User> friends;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
	private String roles; // split by ',' to separate roles

    private String avatar;

    @Column(nullable = false)
    private int elo;

    @OneToMany
	@JoinColumn(name = "sender_id")
	private List<Message> sent = new ArrayList<>();
	@OneToMany
	@JoinColumn(name = "recipient_id")	
	private List<Message> received = new ArrayList<>();

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

	public String getPassword(){
		return this.password;
	}

	public String getRoles(){
		return this.roles;
	}

	public String getUsername(){
		return this.username;
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
	
}