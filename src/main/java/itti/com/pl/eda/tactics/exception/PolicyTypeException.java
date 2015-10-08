package itti.com.pl.eda.tactics.exception;

public class PolicyTypeException extends AbstractPoldException{

	private static final long serialVersionUID = 1L;

	public enum PolicyTypeExceptionType{
		EmptyName,
		EmptyDbName,
		EmptySwrlRule, 
		FileNotFound,
	}

	static{

		descriptions.put(PolicyTypeExceptionType.EmptyName.name(), "Empty policy type name");
		descriptions.put(PolicyTypeExceptionType.EmptyDbName.name(), "Empty policy type database name");
		descriptions.put(PolicyTypeExceptionType.EmptySwrlRule.name(), "Empty policy type swrl rule");
	}
	public PolicyTypeException(PolicyTypeExceptionType type) {
		super(type.name());
	}

	public PolicyTypeException(PolicyTypeExceptionType type, String message) {
		super(type.name(), message);
	}
}
