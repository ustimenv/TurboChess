package es.ucm.fdi.iw.turbochess;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class GeneralUtils {
    public static List<Usario> JSONtoList(final String pathToJSON) {
        try {
            Usario[] usarios = new ObjectMapper().readValue(new File(pathToJSON), Usario[].class);
            return Arrays.asList(usarios);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    // TODO reemplazar la funcion arriba con una generic
    // public static <T> List<T> JSONtoList(final TypeReference<T> type, final String jsonPacket) {
    //     T data = null;

    //     try {
    //         data = new ObjectMapper().readValue(jsonPacket, type);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return data;
    // }
}
