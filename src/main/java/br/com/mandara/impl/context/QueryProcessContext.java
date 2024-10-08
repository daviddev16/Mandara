package br.com.mandara.impl.context;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.UUID;

public final class QueryProcessContext {

    private final ResultSet resultSet;
    private final ResultSetMetaData resultSetMetaData;
    private final UUID queryId;
    
    public QueryProcessContext(ResultSet resultSet, UUID queryId) throws SQLException {
        this.resultSet = resultSet;
        this.resultSetMetaData = resultSet.getMetaData();
        this.queryId = queryId;
    }
    
    public UUID getQueryId() {
        return queryId;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public ResultSetMetaData getResultSetMetaData() {
        return resultSetMetaData;
    }
    
}
