package br.com.easyorm.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;

import br.com.easyorm.core.IEntityQueryExecutor;
import br.com.easyorm.core.Stopwatch;
import br.com.easyorm.core.dialect.IDialect;
import br.com.easyorm.core.queries.IDQLQuery;
import br.com.easyorm.entity.EntityManager;
import br.com.easyorm.impl.dialect.PostgreSQLDialect;
import br.com.easyorm.impl.executor.EntityQueryExecutorImpl;

public class Main {

    public static void main(String[] args) throws SQLException {

        //Stopwatch.enableStopwatch();
        
        EntityManager entityManager = EntityManager.getInstance();

        Connection connection = DriverManager.getConnection(toJdbcString("127.0.0.1", "5432", "DB_NOMINA_SHOP"), "postgres", "abc@123");

        IDialect dialect = new PostgreSQLDialect();

        IEntityQueryExecutor executorImpl = new EntityQueryExecutorImpl(entityManager, connection, dialect);

        /*
        IDQLQuery<Estoque> selectEstoqueQuery = executorImpl.executePreparedSelectQuery("SELECT * FROM Estoque", Estoque.class);
        
        try {
            selectEstoqueQuery.getDataSet().forEach(System.out::println);
        } catch (QueryProcessorException e) {
            e.printStackTrace();
        }*/
        
        
        IDQLQuery<Usuario> selectQuery = executorImpl.executeSelectQuery("SELECT * FROM Usuario LIMIT 1000000", Usuario.class);

        selectQuery.getParameterized().setParameterString(0, "");
        
        Collection<Usuario> usuarios;
        try {

            long last = System.currentTimeMillis();

            usuarios = selectQuery.getDataSet();
            //usuarios.forEach(System.out::println);

            System.out.println( (System.currentTimeMillis() - last) + "ms" );


        } catch (Exception e) {
            e.printStackTrace();
        }

/*
        for (int i = 0; i < 1100; i++) {
            Usuario s = new Usuario("David"+i, "177.087.007-5"+i, 3);
            IDMLQuery<Usuario> insertQuery = executorImpl.executeInsertQuery(s);

            try {
                long last = System.currentTimeMillis();
                insertQuery.execute();
                System.out.println( (System.currentTimeMillis() - last) + "ms" );
            } catch (QueryProcessorException e) {
                e.printStackTrace();
            }
            s = null;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }*/

    }

    
    
    static String toJdbcString(String host, String port, String database) {
        return String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
    }


}
