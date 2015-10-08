package itti.com.pl.eda.tactics.policy;

import itti.com.pl.eda.tactics.utils.StringUtils;

/**
 * logical operator is used to construct policy actions/conditions
 * defines available relations between formula elements
 * @author marcin
 *
 */
public enum LogicalOperator {

	And,
	Or;


	/**
	 * parses input string and returns valid LogicalOperator value
	 * @param string input string
	 * @return valid LogicalOperator, or null if string is invalid
	 */
	public static LogicalOperator getValue(String string){
		if(StringUtils.isNullOrEmpty(string)){
			return null;
		}else{
			for (LogicalOperator operator : LogicalOperator.values()) {
				if(operator.name().equalsIgnoreCase(string)){
					return operator;
				}
			}
			return null;
		}
	}
}
