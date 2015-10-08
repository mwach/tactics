package itti.com.pl.eda.tactics.policy;

import itti.com.pl.eda.tactics.utils.StringUtils;

/**
 * list of values which can be used as policy triple operators
 * @author marcin
 *
 */
public enum PolicyTripleOperator {

	Equals("="),
	NotEquals("!="),
	LessThanOrEquals("<="),
	LessThan("<"),
	GreaterThanOrEquals(">="),
	GreaterThan(">"),

	Set("");

	private String formula = null;

	/**
	 * constructor
	 * @param formula mathematical formula
	 */
	private PolicyTripleOperator(String formula){
		this.formula= formula;
	}

	/**
	 * returns value of the mathematical formula related to the operator
	 * @return formula
	 */
	public String getLogicalSign(){
		return formula;
	}

	/**
	 * parses given string and returns adequate PolicyTripleOperator
	 * @param item examined string
	 * @return adequate PolicyTripleOperator or null if parsing fails
	 */
	public static PolicyTripleOperator getValue(String item){

		if(StringUtils.isNullOrEmpty(item)){
			return null;

		}else{

			for (PolicyTripleOperator policyTripleOperator : PolicyTripleOperator.values()) {
				if(policyTripleOperator.name().equalsIgnoreCase(item)){
					return policyTripleOperator;
				}
			}
		}
		return null;
	};
}
