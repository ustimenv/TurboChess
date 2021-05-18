package es.ucm.fdi.iw.turbochess.model;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Room{
    static int codeLengths=4;
    static Set<String> roomCodes = new HashSet<>();
    final String code;      // room code
    private CodeGenerator codeGenerator = new CodeGenerator();


    public Room(){
        String code = "";
        while(!roomCodes.contains(code)){
            code = codeGenerator.generate();
        }
        this.code = code;

    }

    private static class CodeGenerator{
        private static final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        private final Random random;

        private CodeGenerator(){
            random = ThreadLocalRandom.current();
        }

        private String generate(){
            StringBuilder sb = new StringBuilder();
            for(int i=0; i<Room.codeLengths; i++){
                sb.append(letters.charAt(random.nextInt(letters.length())));
            }
            return sb.toString();
        }
    }
}
