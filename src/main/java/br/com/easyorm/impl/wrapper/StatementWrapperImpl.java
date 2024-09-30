package br.com.easyorm.impl.wrapper;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;

import br.com.easyorm.core.IStatementWrapper;
import br.com.easyorm.util.Utilities;

class StatementWrapperImpl implements IStatementWrapper {

    private final String sqlQuery;
    private final Statement statement;
    
    public StatementWrapperImpl(Connection connection, String sqlQuery) throws SQLException {
        this.statement = connection.createStatement();
        this.sqlQuery = sqlQuery;
    }
    
    @Override
    public void setParameterObject(String name, Object value) throws SQLException {
        settingParamaterUnsupportedException();
    }

    @Override
    public void setParameterObject(int index, Object value) throws SQLException {
        settingParamaterUnsupportedException();
    }

    @Override
    public void setParameterString(String name, String value) throws SQLException {
        settingParamaterUnsupportedException();        
    }

    @Override
    public void setParameterString(int index, String value) throws SQLException {
        settingParamaterUnsupportedException();    
    }

    @Override
    public void setParameterInt(String name, int value) throws SQLException {
        settingParamaterUnsupportedException();    
    }

    @Override
    public void setParameterInt(int index, int value) throws SQLException {
        settingParamaterUnsupportedException();
    }

    @Override
    public void setParameterLong(String name, long value) throws SQLException {
        settingParamaterUnsupportedException();    
    }

    @Override
    public void setParameterLong(int index, long value) throws SQLException {
        settingParamaterUnsupportedException();        
    }

    @Override
    public void setParameterTimestamp(String name, Timestamp value) throws SQLException {
        settingParamaterUnsupportedException();
    }

    @Override
    public void setParameterTimestamp(int index, Timestamp value) throws SQLException {
        settingParamaterUnsupportedException();
    }

    @Override
    public void setParameterDate(String name, Date value) throws SQLException {
        settingParamaterUnsupportedException();    
    }

    @Override
    public void setParameterDate(int index, Date value) throws SQLException {
        settingParamaterUnsupportedException();    
    }

    @Override
    public void setParameterDate(String name, Date value, Calendar cal) throws SQLException {
        settingParamaterUnsupportedException();    
    }

    @Override
    public void setParameterDate(int index, Date value, Calendar cal) throws SQLException {
        settingParamaterUnsupportedException();
    }
    
    private void settingParamaterUnsupportedException() {
        throw new UnsupportedOperationException("Setting parameters are not allowed in non-prepared statements.");
    }
    
    @Override
    public void executeStatement() throws SQLException {
        statement.executeUpdate(sqlQuery);
    }
    
    @Override
    public ResultSet executeQuery() throws SQLException {
        return statement.executeQuery(sqlQuery);
    }
    
    @Override
    public void closeQuietly() {
        Utilities.closeQuietly(statement);
    }

}
