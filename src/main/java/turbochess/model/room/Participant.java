package turbochess.model.room;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import turbochess.model.User;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name= "room_code")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Participant(Room room, User user){
        this.room = room;
        this.user = user;
    }

    @Column(name = "current_bet")
    private int currentBet = 0;             // on the last transaction we will commit the bet manually

    @Enumerated(EnumType.STRING)
    private Role role;                      // we'll store the roles as strings for clarity

    public enum Role implements Serializable{
        PLAYER1, PLAYER2, OBSERVER;
    }

    @Enumerated(EnumType.STRING)
    private Colour colour;


    public enum Colour implements Serializable{
        WHITE, BLACK, NONE;
    }
    public String getColourString(){
        switch (colour){
            case WHITE: return "w";
            case BLACK: return "b";
            default:    return "-";
        }
    }

    @Override
    public int hashCode(){
        return room.hashCode()*user.hashCode();
    }

    @Override
    public boolean equals(Object other){
        if( !(other instanceof Participant) ){
            return false;
        } else{
            return this.user.getUsername().equals(((Participant) other).user.getUsername()) &&
                   this.room.getCode().equals(((Participant) other).room.getCode());
        }
    }

    public String toJSON(){
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module =new SimpleModule("ParticipantSerialiser");
        module.addSerializer(Participant.class, new ParticipantSerialiser());
        mapper.registerModule(module);


        try{
            return mapper.writeValueAsString(this);
        } catch(JsonProcessingException e){
            e.printStackTrace();
            return null;
        }
    }
    private static class ParticipantSerialiser extends StdSerializer<Participant>{
        protected ParticipantSerialiser(){
            this(null);
        }
        protected ParticipantSerialiser(Class<Participant> t){
            super(t);
        }

        @Override
        public void serialize(Participant participant, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException{
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("id", participant.getId());
            jsonGenerator.writeStringField("username", participant.user.getUsername());
            jsonGenerator.writeStringField("room", participant.room.getCode());
            jsonGenerator.writeNumberField("bet", participant.getCurrentBet());
            jsonGenerator.writeStringField("role", participant.getRole().toString());   //todo
            jsonGenerator.writeStringField("colour", participant.getColour().toString());   //todo
            jsonGenerator.writeEndObject();
        }
    }
}
