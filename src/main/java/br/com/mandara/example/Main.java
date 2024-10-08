package br.com.mandara.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

import br.com.mandara.core.IEntityManager;
import br.com.mandara.core.IEntityQueryExecutor;
import br.com.mandara.core.internal.Stopwatch;
import br.com.mandara.core.queries.IDMLQuery;
import br.com.mandara.core.queries.IDQLQuery;
import br.com.mandara.example.Estoque.EstoquePK;
import br.com.mandara.exception.QueryProcessorException;
import br.com.mandara.impl.GenericEntityManager;
import br.com.mandara.impl.RepositoryProxyDelegator;

public class Main {
    
    public static void deletes(IEntityQueryExecutor executorImpl) throws SQLException {

        Usuario s = new Usuario("David Duarte", "177.087.007-52", 1);
        s.setId(1);
        
        IDMLQuery<Usuario> deleteQuery = executorImpl.executeDeleteQuery(s);
        
        try {
            deleteQuery.execute();
        } catch (QueryProcessorException e) {
            e.printStackTrace();
        }
     
        Estoque estoque = new Estoque(new EstoquePK(1, 120, Timestamp.valueOf("2024-03-20 00:00:00")), (double) 4);
        
        IDMLQuery<Estoque> deleteQuery1 = executorImpl.executeDeleteQuery(estoque);
        
        try {
            deleteQuery1.execute();
        } catch (QueryProcessorException e) {
            e.printStackTrace();
        }
        
    }
    
    public static void inserts(IEntityQueryExecutor executorImpl) throws SQLException {
     
        for (int i = 0; i < 5; i++) {
            Usuario s = new Usuario("David"+i, "177.087.007-5"+i, 3);
            IDMLQuery<Usuario> insertQuery = executorImpl.executeInsertQuery(s);

            try {
                long last = System.currentTimeMillis();
                Usuario us = insertQuery.executeReturning();
                System.out.println(us);
                System.out.println( (System.currentTimeMillis() - last) + "ms" );
            } catch (QueryProcessorException e) {
                e.printStackTrace();
            }
            s = null;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
    }
    
    public static void updates(IEntityQueryExecutor executorImpl) throws SQLException {
        
        Usuario usuario = new Usuario("David Duarte", "177.087.007-53", 1);
        usuario.setId(1);
        
        IDMLQuery<Usuario> updateQuery = executorImpl.executeUpdateQuery(usuario);
        
        try {
            updateQuery.execute();
        } catch (QueryProcessorException e) {
            e.printStackTrace();
        }
        
        
        Estoque estoque = new Estoque(new EstoquePK(1, 120, Timestamp.valueOf("2024-03-20 00:00:00")), (double) 4);
        
        IDMLQuery<Estoque> updateQuery1 = executorImpl.executeUpdateQuery(estoque);
        
        try {
            updateQuery1.execute();
        } catch (QueryProcessorException e) {
            e.printStackTrace();
        }
        
        
    }
    
    public static void selects(IEntityQueryExecutor executorImpl) throws SQLException {

                
        IDQLQuery<Estoque> selectEstoqueQuery = executorImpl.executePreparedSelectQuery("SELECT * FROM Estoque", Estoque.class);
        
        try {
            selectEstoqueQuery.getDataSet().forEach(System.out::println);
        } catch (QueryProcessorException e) {
            e.printStackTrace();
        }
        
        /*
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
        }*/
        
    }

    public static void main(String[] args) throws SQLException {

        Stopwatch.enableStopwatch();
        
        Connection connection = DriverManager.getConnection(toJdbcString("127.0.0.1", "5432", "DB_NOMINA_SHOP"), "postgres", "abc@123");

        IEntityManager entityManager = GenericEntityManager.getInstance();

        entityManager.register(Usuario.class);
        
        IEntityQueryExecutor executorImpl = IEntityQueryExecutor.getImplementation(entityManager, connection);
        
        RepositoryProxyDelegator proxyDelegator = new RepositoryProxyDelegator();
        
        UsuarioRepository usuarioRepository = proxyDelegator.delegateProxy(entityManager, connection, UsuarioRepository.class);
        
        for (int i = 0; i < 20; i++) {

            Stopwatch.beginStopwatch("Teste");
            Optional<Usuario> optUsuario = usuarioRepository.findEntityById(2002213);
            Stopwatch.endStopwatch("Teste");

            if (optUsuario.isPresent())
                System.out.println(optUsuario.get());


            System.out.println(usuarioRepository.toString());

            Stopwatch.summary();
        }
        //inserts(executorImpl);
    }

    
    
    static String toJdbcString(String host, String port, String database) {
        return String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
    }


}
