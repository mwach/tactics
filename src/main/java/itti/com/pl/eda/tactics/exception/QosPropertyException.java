package itti.com.pl.eda.tactics.exception;

public class QosPropertyException extends AbstractPoldException{

	private static final long serialVersionUID = 1L;


	public enum QosPropertyExceptionType{
		PropertyNameIsNull,
		InvalidPropertyName
	}

	static{
	}

	public QosPropertyException(QosPropertyExceptionType type){
		super(type.name());
	}

	public QosPropertyException(QosPropertyExceptionType type, String desc){
		super(type.name(), desc);
	}
}
