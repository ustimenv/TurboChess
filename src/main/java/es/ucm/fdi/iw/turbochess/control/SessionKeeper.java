package es.ucm.fdi.iw.turbochess.control;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public enum SessionKeeper{              // TODO change to a more descriptive name
    INSTANCE;

    // code length may be increased theoretically
    private final int INITIAL_CODE_LENGTH = 3;
    // number of times we will attempt to generate a code of length N before attempting to generate one of length N+1
    private final int MAX_ATTEMPTS = 100;

    // TODO query the DB directly and populate usedCodes from there
    private Set<String> usedCodes = new HashSet<>();                  // room codes currently in use
    private int codeLength = INITIAL_CODE_LENGTH;

    public int randomInt(int lower, int upper){     // lower inclusive, upper exclusive
        return ThreadLocalRandom.current().nextInt(lower, upper);
    }

    public String generateRoomCode(){
        int numAttempts = 0;
        String code = CodeGenerator.generate(codeLength);

        while(usedCodes.contains(code)){
            code = CodeGenerator.generate(codeLength);
            if(numAttempts++ > MAX_ATTEMPTS){
                codeLength++;           // ideally we'd want a way to trim codes at some point but the server isn't going to be running that long
            }
        }
        usedCodes.add(code);
        return code;
    }

    void deleteRoom(String code){
        usedCodes.remove(code);
    }


    private static class CodeGenerator{
        private static final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        private static String generate(int length){        // generate a "random" string of given length
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < length; i++){
                sb.append(letters.charAt(SessionKeeper.INSTANCE.randomInt(0, letters.length())));
            }
            return sb.toString();
        }
    }

}