package itti.com.pl.eda.tactics.exception;

public class RefinementCalculatorException extends AbstractPoldException{

	private static final long serialVersionUID = 1L;

	public enum RefinementCalculatorExceptionType{
		NullQoSParameter, 
		UnrecognizedApplicationType, 
		EmptyDataSet, 
		ParseException, 
		NullCalculateWeight,
	};

	static{

		descriptions.put(RefinementCalculatorExceptionType.NullQoSParameter.name(), "QoS parameter's name is NULL");
		descriptions.put(RefinementCalculatorExceptionType.UnrecognizedApplicationType.name(), "Unrecognized Application type");
		descriptions.put(RefinementCalculatorExceptionType.EmptyDataSet.name(), "Initialized data set is empty");
		descriptions.put(RefinementCalculatorExceptionType.ParseException.name(), "Exception during parsing DB data");
	}

	public RefinementCalculatorException(RefinementCalculatorExceptionType excType, String desc){
		super(excType.name(), desc);
	}

	public RefinementCalculatorException(RefinementCalculatorExceptionType excType){
		super(excType.name());
	}
}
