package itti.com.pl.eda.tactics.exception;

public class MessageException extends AbstractPoldException{

	private static final long serialVersionUID = 1L;

	public static enum MessageExceptionType{
		MessageIsNull, 
		MessageIsEmpty, 
		MessageHasWrongFormat, 
		MessageHasInvalidType, 
		MessageHasInvalidArgument, 
		ParseException, 
		MessageHasInvalidOperatorParameter; 
	};

	static{
		descriptions.put
				(MessageExceptionType.MessageIsNull.name(), "Message is null");
		descriptions.put
				(MessageExceptionType.MessageIsEmpty.name(), "Message is empty");
		descriptions.put
				(MessageExceptionType.MessageHasWrongFormat.name(), "Message has wrong format");
		descriptions.put
				(MessageExceptionType.MessageHasInvalidType.name(), "Message has invalid type");
		descriptions.put
				(MessageExceptionType.MessageHasInvalidType.name(), "Message has invalid argument");
		descriptions.put
				(MessageExceptionType.ParseException.name(), "Argument parse exception");
		descriptions.put
				(MessageExceptionType.ParseException.name(), "Operator parameter parse exception");
	}


	public MessageException(MessageExceptionType type, String msg){
		super(type.name(), msg);
	}

	public MessageException(MessageExceptionType type){
		super(type.name());
	}
}