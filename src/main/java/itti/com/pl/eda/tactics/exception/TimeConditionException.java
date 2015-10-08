package itti.com.pl.eda.tactics.exception;

public class TimeConditionException extends AbstractPoldException {

	private static final long serialVersionUID = 1L;

	public enum TimeConditionExceptionType{
		PeriodIsNull, 
		BeginIsNull, 
		EndIsNull, 
		InvalidValue, 
		TranslateException
		
	};

	public TimeConditionException(TimeConditionExceptionType type) {
		super(type.name());
	}

	public TimeConditionException(TimeConditionExceptionType type,
			String desc) {
		super(type.name(), desc);
	}

}
