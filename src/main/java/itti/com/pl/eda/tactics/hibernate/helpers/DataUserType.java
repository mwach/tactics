package itti.com.pl.eda.tactics.hibernate.helpers;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import itti.com.pl.eda.tactics.policy.DateUnit;

/**
 * implementation of extended Date object for Hibernate use
 * @author marcin
 *
 */
public class DataUserType  implements UserType{

	public Object assemble(Serializable arg0, Object arg1)
			throws HibernateException {
		return arg0;
	}

	public Object deepCopy(Object arg0) throws HibernateException {
		return arg0;
	}

	public Serializable disassemble(Object arg0) throws HibernateException {
		return (Serializable)arg0;
	}

	public boolean equals(Object arg0, Object arg1) throws HibernateException {

		if(arg0 == arg1){
			return true;
		}
		if(arg0 == null || arg1 == null){
			return false;
		}
		return arg0.equals(arg1);	
	}

	public int hashCode(Object arg0) throws HibernateException {

		if(arg0 == null){
			return 0;
		}else{
			return arg0.hashCode();
		}
	}

	public boolean isMutable() {
		return false;
	}

	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
			throws HibernateException, SQLException {

		if(rs.wasNull()){
			return null;
		}else{
			String dateString = rs.getString(names[0]);
			return DateUnit.parseString(dateString);
		}
	}

	public void nullSafeSet(PreparedStatement stmt, Object value, int index)
			throws HibernateException, SQLException {

		if(value == null){
			stmt.setNull(index, Types.VARCHAR);
		}else{
			DateUnit param = (DateUnit)value;
			String dateParsed = DateUnit.parseDate(param);
			stmt.setString(index, dateParsed);
		}
	}

	public Object replace(Object arg0, Object arg1, Object arg2)
			throws HibernateException {
		return null;
	}

	public Class<DateUnit> returnedClass() {
		return DateUnit.class;
	}

	private static final int[] SQL_TYPES = {Types.VARCHAR};

	public int[] sqlTypes() {
		return SQL_TYPES;
	}

	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor arg2, Object arg3)
			throws HibernateException, SQLException {
		if(rs.wasNull()){
			return null;
		}else{
			String dateString = rs.getString(names[0]);
			return DateUnit.parseString(dateString);
		}
	}

	public void nullSafeSet(PreparedStatement stmt, Object value, int index, SessionImplementor arg3)
			throws HibernateException, SQLException {
		if(value == null){
			stmt.setNull(index, Types.VARCHAR);
		}else{
			DateUnit param = (DateUnit)value;
			String dateParsed = DateUnit.parseDate(param);
			stmt.setString(index, dateParsed);
		}
	}

}
