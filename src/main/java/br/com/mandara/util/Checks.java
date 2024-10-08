package br.com.mandara.util;

import br.com.mandara.exception.StateException;

public final class Checks {

    public static void stateNotNull(Object object, String target) {
        state((object == null), String.format("%s should not be null.", target));
    }

    public static void state(boolean stateFlag, String message) {
        if (stateFlag) throw new StateException(message);
    }
    
}
