package itti.com.pl.eda.tactics.exception;

public class PoldNetworkException extends AbstractPoldException{

	private static final long serialVersionUID = 1L;

	public static enum PoldNetworkExceptionType{
		ConnectionTimeout, 
		InitErrors,
		NullArgument
	};

	static{
		descriptions.put(PoldNetworkExceptionType.ConnectionTimeout.name(), "Connection timeout");
	}


	public PoldNetworkException(PoldNetworkExceptionType exceptionType, String desc) {
		super(exceptionType.name(), desc);
	}
}
