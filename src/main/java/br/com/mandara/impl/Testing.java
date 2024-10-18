package br.com.mandara.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import br.com.mandara.core.IEntityQueryExecutor;

public final class Testing {

    private static final String DB_NAME = "DB_TEST_01";
    
    public static IEntityQueryExecutor getTestingExecutor() throws SQLException {
        Connection connection = DriverManager.getConnection(
                toJdbcString("127.0.0.1", "5432", DB_NAME), "postgres", "abc@123");
        return IEntityQueryExecutor.getImplementation(
                GenericEntityManager.getInstance(), 
                connection);
    }
    
    static String toJdbcString(String host, String port, String database) {
        return String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
    }
    
}
