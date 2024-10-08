package br.com.mandara.exception;


import java.lang.reflect.Field;

import br.com.mandara.annotation.Id;
import br.com.mandara.util.Reflections;

public final class StaticEntityException {

    public static StateException alreadyRegisteredStateException(Class<?> entityClassType) {
        return new StateException(entityClassType.getName() + " already registered."); 
    }
    
    public static StateException unpermittedTypeStateException(Field field) {
        return new StateException(Reflections.fieldToString(field) + " uses a unpermitted type."); 
    }
    
    public static StateException IdOutOfBoundsStateException(Class<?> entityClassType) {
        return new StateException(entityClassType.getName() + " should not have more "
                + "than a field annotated with " + Id.class.getName() + "."); 
    }
    
    public static StateException noPkDefinedStateException(Class<?> entityClassType) {
        return new StateException(entityClassType.getName() + " should have at least one primary key."); 
    }
    
    public static StateException idDefinedMoreThanOnceStateException(Class<?> entityClassType) {
        return new StateException(entityClassType.getName() + " should have at least one primary key."); 
    }
    
    public static StateException noFieldDefinedStateException(Class<?> entityClassType) {
        return new StateException("There is not field defined on " + entityClassType.getName()); 
    }
    
    public static StateException recursiveIdStateException(Field field) {
        return new StateException(Reflections.fieldToString(field) 
                + " should not be annotated with " + Id.class.getName()); 
    }
    
}
