package es.ucm.fdi.iw.turbochess.control;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public enum CodeGenerator{
    INSTANCE;

    // code length may be increased theoretically
    private final int INITIAL_CODE_LENGTH = 3;
    // number of times we will attempt to generate a code of length N before attempting to generate one of length N+1
    private final int MAX_ATTEMPTS = 100;                   // todo potentially scale exponentially w.r.t code length
    private final int PRUNING_PERIOD=5;                     // how often we will try to shorten the codes

    private Set<String> usedCodes = new HashSet<>();        // cache recently used codes to not make avoidable DB calls
    private int codeLength = INITIAL_CODE_LENGTH;

    private final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public int randomInt(int lower, int upper){     // lower inclusive, upper exclusive
        return ThreadLocalRandom.current().nextInt(lower, upper);
    }

    public String generateRoomCode(){
        if(codeLength % PRUNING_PERIOD == 0){
            pruneCodes();
        }

        int numAttempts = 0;
        String code = generate(codeLength);

        while(usedCodes.contains(code)){
            code = generate(codeLength);
            if(numAttempts++ > MAX_ATTEMPTS){
                codeLength++;
            }
        }
        usedCodes.add(code);
        return code;
    }

    private void pruneCodes(){
        usedCodes.clear();
        codeLength = Math.max(codeLength-(PRUNING_PERIOD-1), INITIAL_CODE_LENGTH);   // floor the length
    }

    private String generate(int length){        // generate a "random" string of given length
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++){
            sb.append(letters.charAt(randomInt(0, letters.length())));
        }
        return sb.toString();
    }

}