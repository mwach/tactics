package itti.com.pl.eda.tactics.policy;

public enum Condition{

	LessThan("<"),
	LessThanOrEquals("<="),
	Equals("="),
	GreaterThanOrEquals(">="),
	GreaterThan(">"),
	;

	private Condition(String formValue){
		this.formValue = formValue;
	}

	private String formValue;
	public String getFormValue(){
		return formValue;
	}

	public static Condition getCondition(Object obj){
		if(obj != null ){
			String value = String.valueOf(obj);
			for (Condition condition : Condition.values()) {
				if(condition.name().equals(value) || condition.formValue.equals(value)){
					return condition;
				}
			}
		}
		return null;
	}
};
