package itti.com.pl.eda.tactics.hibernate.helpers;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;


/**
 * implementation of Boolean data type for Hibernate use
 * @author marcin
 *
 */
public class BooleanUserType implements UserType{

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
		}
		return rs.getBoolean(names[0]);
	}


	public void nullSafeSet(PreparedStatement stmt, Object value, int index)
	throws HibernateException, SQLException {

		if(value == null){
			stmt.setNull(index, Types.BOOLEAN);
		}else{
			boolean param = (Boolean)value;
			stmt.setBoolean(index, param);
		}
	}

	public Object replace(Object arg0, Object arg1, Object arg2)
			throws HibernateException {
		return null;
	}

	public Class<Boolean> returnedClass() {
		return Boolean.class;
	}

	private static final int[] SQL_TYPES = {Types.BOOLEAN};

	public int[] sqlTypes() {
		return SQL_TYPES;
	}

	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor arg2, Object arg3)
			throws HibernateException, SQLException {
		if(rs.wasNull()){
			return null;
		}
		return rs.getBoolean(names[0]);
	}

	public void nullSafeSet(PreparedStatement stmt, Object value, int index, SessionImplementor arg3)
			throws HibernateException, SQLException {
		if(value == null){
			stmt.setNull(index, Types.BOOLEAN);
		}else{
			boolean param = (Boolean)value;
			stmt.setBoolean(index, param);
		}
		
	}

}
