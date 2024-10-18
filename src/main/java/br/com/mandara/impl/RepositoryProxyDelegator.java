package br.com.mandara.impl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import br.com.mandara.core.IEntityManager;
import br.com.mandara.core.IEntityQueryExecutor;
import br.com.mandara.core.IRepository;
import br.com.mandara.core.queries.IDQLQuery;
import br.com.mandara.entity.EntityMetadata.EntityIdentification;
import br.com.mandara.example.Estoque;
import br.com.mandara.example.Usuario;

public class RepositoryProxyDelegator {

    @SuppressWarnings("unchecked")
    public <T extends IRepository<?, ?>> T delegateProxy(
            IEntityManager entityManager, 
            Connection connection, Class<? super T> repositoryClassType) {
        return (T) Proxy.newProxyInstance(
                ClassLoader.getSystemClassLoader(), 
                new Class<?>[] { repositoryClassType }, 
                new RepositoryProxyImpl(entityManager, connection, repositoryClassType));
    }

    public static final class RepositoryProxyImpl implements InvocationHandler {
        
        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface MethodImplAnn
        {
            boolean optional() default false;
            String name();
            String origin();
        }
        
        public static class MethodImplementationHolder 
        {
            private Method method;
            private String name;
            private String origin;
            private boolean optional;

            public MethodImplementationHolder(Method method, String name, String origin, boolean optional) {
                this.method = method;
                this.name = name;
                this.origin = origin;
                this.optional = optional;
            }
        }
        
        public static class EntityRepositoryInformation 
        {
            private final Class<?> entityClassType;
            private final Class<?> identificationClassType;

            public EntityRepositoryInformation(Class<?> entityClassType, Class<?> identificationClassType) {
                this.entityClassType = entityClassType;
                this.identificationClassType = identificationClassType;
            }

            public Class<?> getEntityClassType() {
                return entityClassType;
            }

            public Class<?> getIdentificationClassType() {
                return identificationClassType;
            }
            
        }
        
        private static final Map<String, MethodImplementationHolder> methodImplMap = 
                new HashMap<String, MethodImplementationHolder>();
        
        static 
        {
            Method[] methods = RepositoryProxyImpl.class.getDeclaredMethods();
            for (Method method : methods) {
                MethodImplAnn methodImplAnn = method.getDeclaredAnnotation(MethodImplAnn.class);
                if (methodImplAnn != null) 
                {
                    MethodImplementationHolder implementationHolder = 
                            new MethodImplementationHolder(method,
                            methodImplAnn.name(), methodImplAnn.origin(), methodImplAnn.optional());
                    methodImplMap.put(methodImplAnn.name(), implementationHolder);
                }
            }
        }
        
        public static void main(String[] args) {
            
            IEntityManager entityManager = GenericEntityManager.getInstance();
            entityManager.register(Estoque.class);
            
            EntityIdentification identification = entityManager
                    .getMetadataOf(Estoque.class)
                    .getEntityIdentification();
            
            
        }
        
        private final Class<?> repositoryClassType;
        private final EntityRepositoryInformation entityRepositoryInfo;
        private final IEntityQueryExecutor entityQueryExecutor;

        public RepositoryProxyImpl(
                IEntityManager entityManager, 
                Connection connection, Class<?> repositoryClassType) 
        {
            this.repositoryClassType  = repositoryClassType;
            this.entityRepositoryInfo = resolveEntityRepositoryInformation(repositoryClassType);
            this.entityQueryExecutor  = IEntityQueryExecutor.getImplementation(entityManager, connection);
        }
        
        private EntityRepositoryInformation resolveEntityRepositoryInformation(Class<?> repositoryImplClassType) {
            Type[] genericInterfaceTypes = repositoryImplClassType.getGenericInterfaces();
            
            for (Type genericInterface : genericInterfaceTypes) {
            
                if(!(genericInterface instanceof ParameterizedType) || 
                        !(IRepository.class.isAssignableFrom((Class<?>)genericInterface)))
                    continue;
                
                Type[] actualTypeArguments = 
                        ((ParameterizedType) genericInterface).getActualTypeArguments();
                
                return new EntityRepositoryInformation(
                                ((Class<?>)actualTypeArguments[0]), 
                                ((Class<?>)actualTypeArguments[1]));
            }
            
            return null;
        }

        private Object executeImplementation(MethodImplementationHolder implementationHolder, Object... args) throws Exception {
            try {
                return implementationHolder.method.invoke(this, args);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                // TODO should be better handled tho
                throw e;
            }
        }
        
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            final MethodImplementationHolder implementationHolder = methodImplMap.get(method.getName());
            
            if (implementationHolder == null)
                throw new UnsupportedOperationException(method.getName() + 
                        " was not internally implemented by a proxy.");
            
            Object rawResult = executeImplementation(implementationHolder, args);

            return (implementationHolder.optional) ? Optional.ofNullable(rawResult) : rawResult;
        }

        @Override
        @MethodImplAnn(name = "toString", origin = "RepositoryProxyImpl")
        public String toString() {
            return "RepositoryProxyImpl Proxy Of " + repositoryClassType.getSimpleName() + "[ RepositoryProxyImpl@" + hashCode() + " ]";
        }

        @MethodImplAnn(name = "findEntityById", origin = "IRepository#findEntityById")
        public Object findEntityByIdImpl(Object id) throws Exception {
            
            IDQLQuery<?> query = entityQueryExecutor.executePreparedSelectQuery("SELECT * FROM Usuario WHERE IdUsuario = ?", Usuario.class);

            query.getParameterized().setParameterObject(1, id);

            List<?> dataSet = query.getDataSet();

            if (dataSet.isEmpty())
                return Optional.empty();

            Object object = query.getDataSet().get(0);
            return Optional.of(object);

        }
        
    }
    
}
