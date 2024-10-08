package br.com.mandara.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import br.com.mandara.core.IEntityManager;
import br.com.mandara.core.IEntityQueryExecutor;
import br.com.mandara.core.IRepository;
import br.com.mandara.core.queries.IDQLQuery;
import br.com.mandara.example.Usuario;
import br.com.mandara.exception.QueryProcessorException;

public class RepositoryProxyDelegator {

    @SuppressWarnings("unchecked")
    public <T extends IRepository<?, ?>> T delegateProxy(IEntityManager entityManager, Connection connection, Class<? super T> repositoryClassType) {
        return (T) Proxy.newProxyInstance(
                ClassLoader.getSystemClassLoader(), 
                new Class<?>[] { repositoryClassType }, 
                new RepositoryProxyImpl(entityManager, connection, repositoryClassType));
    }

    public static final class RepositoryProxyImpl implements InvocationHandler {
        
        private final IEntityManager entityManager;
        private final Class<?> repositoryClassType;
        private final IEntityQueryExecutor entityQueryExecutor;

        public RepositoryProxyImpl(IEntityManager entityManager, Connection connection, Class<?> repositoryClassType) {
            this.entityManager = entityManager;
            this.repositoryClassType = repositoryClassType;
            this.entityQueryExecutor = IEntityQueryExecutor.getImplementation(entityManager, connection);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            
            if (method.getName().equals("findEntityById"))
                return proxyfindEntityByIdImpl(args[0]);
            
            else if (method.getName().equals("toString"))
                return toString();

            return null;
        }

        @Override
        public String toString() {
            return "RepositoryProxyImpl Proxy Of " + repositoryClassType.getSimpleName() + "[ RepositoryProxyImpl@" + hashCode() + " ]";
        }

        public Optional<Object> proxyfindEntityByIdImpl(Object id) {
            try {
                IDQLQuery<?> query = entityQueryExecutor.executePreparedSelectQuery(
                        "SELECT * FROM Usuario WHERE IdUsuario = ?", Usuario.class);
                
                query.getParameterized().setParameterObject(1, id);

                List<?> dataSet = query.getDataSet();
                
                if (dataSet.isEmpty())
                    return Optional.empty();
                
                Object object = query.getDataSet().get(0);
                return Optional.of(object);

            } catch (SQLException | QueryProcessorException e) {
                e.printStackTrace();
            }
            
            return Optional.empty();
        }
        
    }
    
}
