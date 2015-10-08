package itti.com.pl.eda.tactics.exception;

public class PolicyManagerException extends AbstractPoldException {

	private static final long serialVersionUID = 1L;

	public enum PolicyManagerExceptionType{
		OntologyIsNull
	};

	public PolicyManagerException(PolicyManagerExceptionType type,	String desc) {

		super(type.name(), desc);
	}

}
