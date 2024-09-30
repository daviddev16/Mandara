package br.com.easyorm.impl.wrapper;

import java.sql.Connection;
import java.sql.SQLException;

import br.com.easyorm.core.IStatementWrapper;
import br.com.easyorm.core.StatementType;

public final class StatementWrapperFactory {

    public static IStatementWrapper get(Connection connection, String sqlQuery, StatementType statementType) 
            throws SQLException
    {
        if (statementType == StatementType.NONE) 
            return new StatementWrapperImpl(connection, sqlQuery);
        
        else if (statementType == StatementType.PREPARED) 
            return new PreparedStatementWrapperImpl(connection, sqlQuery);
        
        throw new UnsupportedOperationException("Unsupported "
                + "type of Statement [" + statementType + "]");
    }
    
}
