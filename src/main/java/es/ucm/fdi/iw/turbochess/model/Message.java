package es.ucm.fdi.iw.turbochess.model;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Data;
import lombok.Getter;
import lombok.AllArgsConstructor;


@Entity
@NamedQueries({
	@NamedQuery(name="Message.countUnread",
	query="SELECT COUNT(m) FROM Message m "
			+ "WHERE m.recipient.id = :userId AND m.dateRead = null")
})
@Data
public class Message {
	
	private static Logger log = LogManager.getLogger(Message.class);	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@ManyToOne
	private User sender;
	@ManyToOne
	private User recipient;
	private String text;
	
	private LocalDateTime dateSent;
	private LocalDateTime dateRead;
	
	
    // @Getter
    // @AllArgsConstructor
	// public static class Transfer {
	// 	private String from;
	// 	private String to;
	// 	private String sent;
	// 	private String received;
	// 	private String text;
	// 	long id;
	// 	public Transfer(Message m) {
	// 		this.from = m.getSender().getUsername();
	// 		this.to = m.getRecipient().getUsername();
	// 		this.sent = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(m.getDateSent());
	// 		this.received = m.getDateRead() == null ?
	// 				null : DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(m.getDateRead());
	// 		this.text = m.getText();
	// 		this.id = m.getId();
	// 	}
	// }

	// @Override
	// public Transfer toTransfer() {
	// 	return new Transfer(sender.getUsername(), recipient.getUsername(), 
	// 		DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dateSent),
	// 		dateRead == null ? null : DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dateRead),
	// 		text, id
    //     );
    // }
	public String toString(){
		Gson gson = new Gson();
		return gson.toJson(this);
	}


	public void setRecipient(User u) {
		recipient = u;
	}


	public void setSender(User u) {
		sender = u;
	}


    public void setDateSent(LocalDateTime t) {
		dateSent = t;
    }


	public void setText(String t) {
		text = t;
	}


	public long getId() {
		return id;
	}
}