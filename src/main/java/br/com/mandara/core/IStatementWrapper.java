package br.com.mandara.core;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IStatementWrapper extends IParameterized {

    ResultSet executeQuery() throws SQLException;
 
    ResultSet executeQuery(String sql) throws SQLException;
    
    void executeStatement() throws SQLException;
    
    void executeStatement(String sql) throws SQLException;
    
    void closeQuietly();
 
}
