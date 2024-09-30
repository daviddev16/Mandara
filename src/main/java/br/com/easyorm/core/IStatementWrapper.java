package br.com.easyorm.core;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IStatementWrapper extends IParameterized {

    ResultSet executeQuery() throws SQLException;
 
    void executeStatement() throws SQLException;
    
    void closeQuietly();
 
}
