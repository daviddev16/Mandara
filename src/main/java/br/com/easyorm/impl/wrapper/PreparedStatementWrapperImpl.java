package br.com.easyorm.impl.wrapper;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.com.easyorm.core.IStatementWrapper;
import br.com.easyorm.util.Utilities;

public class PreparedStatementWrapperImpl implements IStatementWrapper  {

    private final PreparedStatement statement;

    private Map<String, int[]> indexMap;

    public PreparedStatementWrapperImpl(Connection connection, String query) throws SQLException {
        String parsedQuery = parse(query);
        statement = connection.prepareStatement(parsedQuery);
    }

    final String parse(String query) {
        int length = query.length();
        StringBuffer parsedQuery = new StringBuffer(length);
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        int index = 1;
        HashMap<String, List<Integer>> indexes = new HashMap<String, List<Integer>>(10);

        for (int i = 0; i < length; i++) {
            char c = query.charAt(i);
            if (inSingleQuote) {
                if (c == '\'') {
                    inSingleQuote = false;
                }
            } else if (inDoubleQuote) {
                if (c == '"') {
                    inDoubleQuote = false;
                }
            } else {
                if (c == '\'') {
                    inSingleQuote = true;
                } else if (c == '"') {
                    inDoubleQuote = true;
                } else if (c == ':' && i + 1 < length && Character.isJavaIdentifierStart(query.charAt(i + 1))) {
                    int j = i + 2;
                    while (j < length && Character.isJavaIdentifierPart(query.charAt(j))) {
                        j++;
                    }
                    String name = query.substring(i + 1, j);
                    c = '?';
                    i += name.length();

                    List<Integer> indexList = indexes.get(name);
                    if (indexList == null) {
                        indexList = new LinkedList<Integer>();
                        indexes.put(name, indexList);
                    }
                    indexList.add(Integer.valueOf(index));

                    index++;
                }
            }
            parsedQuery.append(c);
        }

        indexMap = new HashMap<String, int[]>(indexes.size());
        for (Map.Entry<String, List<Integer>> entry : indexes.entrySet()) {
            List<Integer> list = entry.getValue();
            int[] intIndexes = new int[list.size()];
            int i = 0;
            for (Integer x : list) {
                intIndexes[i++] = x.intValue();
            }
            indexMap.put(entry.getKey(), intIndexes);
        }

        return parsedQuery.toString();
    }

    private int[] getIndexes(String name) {
        int[] indexes = indexMap.get(name);
        if (indexes == null) {
            throw new IllegalArgumentException("Parameter not found: " + name);
        }
        return indexes;
    }

    @Override
    public void setParameterObject(String name, Object value) throws SQLException {
        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setObject(indexes[i], value);
        }
    }

    @Override
    public void setParameterString(String name, String value) throws SQLException {
        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setString(indexes[i], value);
        }
    }

    public void setParameterInt(String name, int value) throws SQLException {
        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setInt(indexes[i], value);
        }
    }

    @Override
    public void setParameterLong(String name, long value) throws SQLException {
        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setLong(indexes[i], value);
        }
    }

    @Override
    public void setParameterTimestamp(String name, Timestamp value) throws SQLException {
        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setTimestamp(indexes[i], value);
        }
    }

    @Override
    public void setParameterDate(String name, java.sql.Date value) throws SQLException {
        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setDate(indexes[i], value);
        }
    }

    @Override
    public void setParameterDate(String name, java.sql.Date value, Calendar cal) throws SQLException {
        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setDate(indexes[i], value, cal);
        }
    }
    
    @Override
    public void setParameterObject(int index, Object value) throws SQLException 
    {
        statement.setObject(index, value);
    }

    @Override
    public void setParameterString(int index, String value) throws SQLException 
    {
        statement.setString(index, value);
    }

    @Override
    public void setParameterInt(int index, int value) throws SQLException 
    {
        statement.setInt(index, value);
    }

    @Override
    public void setParameterLong(int index, long value) throws SQLException 
    {
        statement.setLong(index, value);   
    }

    @Override
    public void setParameterTimestamp(int index, Timestamp value) throws SQLException 
    {
        statement.setTimestamp(index, value);
    }

    @Override
    public void setParameterDate(int index, Date value) throws SQLException 
    {
        statement.setDate(index, value);    
    }

    @Override
    public void setParameterDate(int index, Date value, Calendar cal) throws SQLException 
    {
        statement.setDate(index, value, cal);
    }

    public PreparedStatement getStatement() {
        return statement;
    }

    public boolean execute() throws SQLException {
        return statement.execute();
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return statement.executeQuery();
    }


    @Override
    public void closeQuietly() 
    {
        Utilities.closeQuietly(statement);
    }

    @Override
    public void executeStatement() throws SQLException 
    {
        statement.executeUpdate();
    }

}