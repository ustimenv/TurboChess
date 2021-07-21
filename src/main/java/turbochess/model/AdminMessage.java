package turbochess.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



@Entity
@Data
@NoArgsConstructor
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
