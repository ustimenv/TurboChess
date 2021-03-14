package es.ucm.fdi.iw.turbochess;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Usario{
    
    public final String id;
    public String plazo;      // plazo en el ranking
    public Object avatar;  // foto del usario
    public String nombre;  // nombre visible a otros jugadores    
    public String liga;       // indica la liga a la cual pertenece el usario(oro, plata etc)
    public String ELO;

    @JsonCreator
    public Usario(@JsonProperty("id") String id,            @JsonProperty("place") String plazo, 
                  @JsonProperty("avatar") Object avatar, @JsonProperty("name") String nombre, 
                  @JsonProperty("league") String liga,      @JsonProperty("ELO") String ELO) {
        this.id = id;
        this.setPlazo(plazo);
        this.setAvatar(avatar);
        this.setNombre(nombre);
        this.setLiga(liga);
        this.setELO(ELO);
    }


    
    



    // Getters & Setters
    public String getNombre() {
        return nombre;
    }
    void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Object getAvatar() {
        return avatar;
    }
    void setAvatar(Object avatar) {
        this.avatar = avatar;
    }

    public String getPlazo() {
        return plazo;
    }
    void setPlazo(String plazo) {
        this.plazo = plazo;
    }

    public String getLiga() {
        return liga;
    }
    void setLiga(String liga) {
        this.liga = liga;
    }
    public String getELO() {
        return ELO;
    }
    void setELO(String eLO) {
        this.ELO = eLO;
    }
}
