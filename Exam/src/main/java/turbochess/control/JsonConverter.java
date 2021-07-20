package turbochess.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

public enum JsonConverter{
    INSTANCE;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public ObjectReader getReader(){
        return MAPPER.reader();
    }

    public ObjectWriter getWriter(){
        return MAPPER.writer();
    }

    public String toJSONString(Object object) throws JsonProcessingException{
        if(object != null){
            return MAPPER.writeValueAsString(object);
        } else  return null;
    }
}
