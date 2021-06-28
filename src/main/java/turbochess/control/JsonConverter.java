package turbochess.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import turbochess.model.room.Room;

public enum JsonConverter{
    INSTANCE;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public ObjectReader getReader(){
        return MAPPER.reader();
    }

    public ObjectWriter getWriter(){
        return MAPPER.writer();
    }

//    public <T> String listToJSONString(List<T> list) throws JsonProcessingException{
//        if(list != null){
//            ArrayNode array = MAPPER.createArrayNode();
//            for(T item : list){
//                JsonNode itemNode = MAPPER.valueToTree(item);
//                array.add(MAPPER.createArrayNode().add(itemNode));
//            }
//
//            System.out.println(array);
//            return array.;
//        } else  return null;
//    }


    public String toJSONString(Object object) throws JsonProcessingException{
        if(object != null){
            return MAPPER.writeValueAsString(object);
        } else  return null;
    }

//    public static <T> T fromJSONString(String jsonString) throws JsonProcessingException{
//        if(jsonString != null && jsonString.length() > 0){
//            return MAPPER.reader().readValueAsString();writeValueAsString(object);
//        } else  return null;
//    }

}
