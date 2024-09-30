package br.com.easyorm.impl.dialect;

public class PostgreSQLDialect extends DefaultDialect {

    @Override
    public String getDialectName() {
        return PostgreSQLDialect.class.getSimpleName();
    }

    @Override
    public String getRDBMSName() {
        return "PostgreSQL";
    }
    
}
