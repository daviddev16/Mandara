package br.com.easyorm.core;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

public interface IParameterized {

    void setParameterObject(String name, Object value) throws SQLException;

    void setParameterObject(int index, Object value) throws SQLException;

    
    void setParameterString(String name, String value) throws SQLException;

    void setParameterString(int index, String value) throws SQLException;

    
    void setParameterInt(String name, int value) throws SQLException;

    void setParameterInt(int index, int value) throws SQLException;


    void setParameterLong(String name, long value) throws SQLException;

    void setParameterLong(int index, long value) throws SQLException;

    
    void setParameterTimestamp(String name, Timestamp value) throws SQLException;

    void setParameterTimestamp(int index, Timestamp value) throws SQLException;

    
    void setParameterDate(String name, java.sql.Date value) throws SQLException;

    void setParameterDate(int index, java.sql.Date value) throws SQLException;

    
    void setParameterDate(String name, java.sql.Date value, Calendar cal) throws SQLException;

    void setParameterDate(int index, java.sql.Date value, Calendar cal) throws SQLException;

}
