package itti.com.pl.eda.tactics.exception;

public class PolicySectionException extends AbstractPoldException{

	private static final long serialVersionUID = 1L;


	public enum PolicySectionExceptionType{
		BothTypesSetted, 
		InvalidQoSValue,
	}

	static{
		descriptions.put(PolicySectionExceptionType.BothTypesSetted.name(), 
				"Both variable-operator-value and elements sections are not null. " +
				"Only one of them is allowed");
		descriptions.put(PolicySectionExceptionType.InvalidQoSValue.name(), 
				"Selected variable is not defined as QoS variable");
	}


	public PolicySectionException(PolicySectionExceptionType type, String msg){
		super(type.name(), msg);
	}
}
