package br.com.easyorm.util;

import br.com.easyorm.exception.StateException;

public final class Checks {

    public static void stateNotNull(Object object, String target) 
    {
        state((object == null), String.format("%s should not be null.", target));
    }

    public static void state(boolean stateFlag, String message) 
    {
        if (stateFlag) throw new StateException(message);
    }
    
}
