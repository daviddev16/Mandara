package br.com.easyorm.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import br.com.easyorm.core.IEntityQueryExecutor;
import br.com.easyorm.core.dialect.IDialect;
import br.com.easyorm.core.queries.IDMLQuery;
import br.com.easyorm.entity.EntityManager;
import br.com.easyorm.exception.QueryProcessorException;
import br.com.easyorm.impl.dialect.PostgreSQLDialect;
import br.com.easyorm.impl.executor.EntityQueryExecutorImpl;

public class Main {

    public static void main(String[] args) throws SQLException {

        EntityManager entityManager = EntityManager.getInstance();

        Connection connection = DriverManager.getConnection(toJdbcString("127.0.0.1", "5432", "DB_NOMINA_SHOP"), "postgres", "abc@123");

        IDialect dialect = new PostgreSQLDialect();

        IEntityQueryExecutor executorImpl = new EntityQueryExecutorImpl(entityManager, connection, dialect);

        /*
        IDQLQuery<Usuario> selectQuery = executorImpl.executeSelectQuery("SELECT NmUsuario, DsCpfCnpj FROM Usuario", Usuario.class);

        Collection<Usuario> usuarios;
        try {

            long last = System.currentTimeMillis();

            usuarios = selectQuery.getDataSet();
           // usuarios.forEach(System.out::println);

            System.out.println( (System.currentTimeMillis() - last) + "ms" );


        } catch (Exception e) {
            e.printStackTrace();
        }*/


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
        }


    }

    static String toJdbcString(String host, String port, String database) {
        return String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
    }


}
