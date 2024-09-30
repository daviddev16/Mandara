package br.com.easyorm.impl.dialect;

import java.util.Collection;
import java.util.StringJoiner;

import br.com.easyorm.core.dialect.IDialect;
import br.com.easyorm.entity.EntityField;
import br.com.easyorm.entity.EntityMetadata;

public abstract class DefaultDialect implements IDialect {

    @Override
    public String generateInsertSQL(EntityMetadata entityMetadata, String schema, Collection<EntityField> entityFields) 
    {
        StringBuilder sql = new StringBuilder();
        
        sql.append("INSERT INTO ");
        
        sql.append(entityMetadata.getSchemaTable());
        
        sql.append(" (");
        
        StringJoiner joiner = new StringJoiner(", ");

        StringJoiner paramJoiner = new StringJoiner(", ");
        
        for (EntityField entityField : entityFields)
        {
            joiner.add(entityField.getFieldName());
            paramJoiner.add("?");
        }

        sql.append(joiner.toString());
        
        joiner = null;
        
        sql.append(") VALUES (");

        sql.append(paramJoiner.toString());
        
        sql.append(");");
        
        paramJoiner = null;
        
        return sql.toString();
    }

}
