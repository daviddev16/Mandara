package br.com.easyorm.core.dialect;

import java.util.Collection;

import br.com.easyorm.entity.EntityField;
import br.com.easyorm.entity.EntityMetadata;

public interface IDialect {

    String generateInsertSQL(
            EntityMetadata entityMetadata, 
            String schema, 
            Collection<EntityField> entityFields);
    
    String getDialectName();
    
    String getRDBMSName();
    
    
}
