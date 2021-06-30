package turbochess.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import turbochess.model.room.Participant;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



@Entity
@Data
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name = "Message.findAll",
                query = "SELECT m FROM AdminMessage m")
})
public class AdminMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)

    private @Getter
    LocalDateTime sendTime;

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private String username;
    private String message;

    public AdminMessage( String username, String message){
        this.sendTime = LocalDateTime.now();
        this.username = username;
        this.message = message;
    }



}
