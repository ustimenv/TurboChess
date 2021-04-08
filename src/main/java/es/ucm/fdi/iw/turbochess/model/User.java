package es.ucm.fdi.iw.turbochess.model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.swing.ImageIcon;

import com.google.gson.Gson;

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
@Table(name = "users")
public class User {
	private static Logger log = LogManager.getLogger(User.class);	

    @Id @GeneratedValue(strategy = GenerationType.TABLE)	// the table auto-increments user IDs
    @Column(name="id")
	private int id;

    @Column(name="username", nullable = false)
    private String username;

	@Column(name="password_hash", nullable = false)
	private String passwordHash;

	@Enumerated(EnumType.ORDINAL)
	private UserRole role;

	@Column(name="elo", nullable = false)
	private int elo;

	@Column(name="avatar")
    private byte[] avatar;

    @OneToMany(targetEntity = User.class)
    private List<User> friends;

    @OneToMany
	@JoinColumn(name = "sender_id")
	private List<Message> sent = new ArrayList<>();
	@OneToMany
	@JoinColumn(name = "recipient_id")	
	private List<Message> received = new ArrayList<>();

    
	public String toString(){
		Gson gson = new Gson();
		return gson.toJson(this);
	}


	public int getId() {
		return id;
	}
	public String getUsername(){
		return username;
	}	
	public String getPasswordHash(){
		return passwordHash;
	}
	public UserRole getRole(){
		return role;
	}
	public int getElo(){
		return elo;
	}


    public void setPassword(String password) {
		passwordHash = password;
    }


    public void setUsername(String username) {
		this.username = username;
    }
	
}