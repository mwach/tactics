package itti.com.pl.eda.tactics.policy;

import java.util.ArrayList;
import java.util.List;

import itti.com.pl.eda.tactics.policy.impl.PolicyTripleImpl;

/**
 * test class
 * for future use
 * @author marcin
 *
 */
public class PolicySectionParser {

	public static PolicyTriple parseFormula(String formula){

		PolicyTriple ps = null;

		if(formula == null || formula.trim().length() == 0){
			return null;
		}

		int bracketStartPos = 0;
		for(int i=0 ; i<formula.length() ; i++){
			if(formula.charAt(i) == '('){
				bracketStartPos = i;
			}else if(formula.charAt(i) == ')'){
				String subFormula = formula.substring(bracketStartPos + 1, i);
				parseFormulaNoBrackets(subFormula);
			}
		}

//		((Delay GreaterThan 10 AND Delay LessThan 30) OR (Throughput GreaterThan 120 AND Throughput LessThan 230))
		LogicalOperator operator = null;

		String[] items = formula.split("[(.)]+");

		if(items.length == 1){
			String item = items[0].trim();
			if(item.length() > 0){
				List<PolicyTriple> psList = parseFormulaNoBrackets(item);
				ps = new PolicyTripleImpl(LogicalOperator.And);
				for (PolicyTriple policySection : psList) {
					ps.addElement(policySection);
				}
			}
			return ps;
		}

		for (String item : items) {
			operator = LogicalOperator.getValue(item);
		}
		if(operator != null){
			ps = new PolicyTripleImpl(operator);
			for (String item : items) {
				if(item.trim().length() > 0 && LogicalOperator.getValue(item) != null){
					PolicyTriple psSub = parseFormula(item);
					ps.addElement(psSub);
				}
			}
		}

		return ps;
	}

	static String logicalMatcher = null;

	static{
		StringBuffer list = new StringBuffer();
		for(int i=0 ; i<LogicalOperator.values().length ; i++){
			LogicalOperator currOper = LogicalOperator.values()[i];
			list.append(currOper.name());
			if(i < LogicalOperator.values().length - 1){
				list.append(" | ");
			}
		}
		logicalMatcher = list.toString();
	}

	private static List<PolicyTriple> parseFormulaNoBrackets(String formula) {

		List<PolicyTriple> returnList = new ArrayList<PolicyTriple>();

		String[] items = formula.split(logicalMatcher);
		for (String item : items) {
			String[] elems = item.split(" ");
			if(elems.length == 3){
				String variable = elems[0];
				String operator = elems[1];
				String value = elems[2];
				PolicyTripleVariable var = PolicyTripleVariable.valueOf(variable);
				PolicyTripleOperator oper = PolicyTripleOperator.valueOf(operator);
				PolicyTriple ps = new PolicyTripleImpl(var, oper, value);
				returnList.add(ps);
			}
		}
		return returnList;
	}
}
