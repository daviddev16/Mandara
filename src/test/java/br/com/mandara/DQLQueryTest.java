package br.com.mandara;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import br.com.mandara.annotation.Entity;
import br.com.mandara.annotation.Id;
import br.com.mandara.core.IEntityQueryExecutor;
import br.com.mandara.core.queries.IDQLQuery;
import br.com.mandara.impl.Testing;
import br.com.mandara.util.Utilities;

@TestInstance(Lifecycle.PER_CLASS)
class DQLQueryTest {

    public static @Entity class Person {
        @Id
        private Integer id;
        private String name;
        private String address;
        
        public Person() {}
    }
    
    IEntityQueryExecutor executor;
    
    @BeforeEach
    void setUp() throws Exception 
    {
        executor = Testing.getTestingExecutor();
        
        assertNotNull(executor);
        assertFalse(executor.getConnection().isClosed());

        final Statement crTableStatement = executor.getConnection().createStatement();
        crTableStatement.execute("CREATE TEMP TABLE Person(Id Serial, Name VarChar(255), Address VarChar(255));");
        crTableStatement.execute("INSERT INTO Person (Name, Address) (SELECT "
                + "('PersonName_' || Generate_Series(1, 1000)) AS Name, "
                + "('Address_'    || Generate_Series(1, 1000)) AS Address);");
    }

    @Test
    void testDQLQueryCount() throws Exception 
    {
        IDQLQuery<Person> selectPersonQuery = executor.executeSelectQuery("SELECT Id FROM Person", Person.class);
        assertEquals(selectPersonQuery.getDataSet().size(), 1000);
    }
    
    @Test
    void testDQLQuerySinglePersonPrepared() throws Exception 
    {
        IDQLQuery<Person> selectPersonQuery = executor.executePreparedSelectQuery("SELECT * FROM Person WHERE Name = :pName", Person.class);
        selectPersonQuery.getParameterized().setParameterString("pName", "PersonName_543");
        
        Person person = selectPersonQuery.getDataSet().get(0);
        
        assertEquals(person.name,    "PersonName_543");
        assertEquals(person.address, "Address_543");
    }
    
    @AfterAll
    void endQueryTest() throws SQLException 
    {
        Utilities.closeQuietly(executor.getConnection());
    }

}
