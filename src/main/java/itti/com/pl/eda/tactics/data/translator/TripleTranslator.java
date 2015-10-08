package itti.com.pl.eda.tactics.data.translator;

import itti.com.pl.eda.tactics.data.model.policy.PolicyHLAction;
import itti.com.pl.eda.tactics.data.model.policy.PolicyHLCondition;
import itti.com.pl.eda.tactics.data.model.policy.PolicyILAction;
import itti.com.pl.eda.tactics.data.model.policy.PolicyILCondition;
import itti.com.pl.eda.tactics.data.model.policy.TripleType;
import itti.com.pl.eda.tactics.policy.DateUnit;
import itti.com.pl.eda.tactics.policy.LogicalOperator;
import itti.com.pl.eda.tactics.policy.PolicyTriple;
import itti.com.pl.eda.tactics.policy.PolicyTripleOperator;
import itti.com.pl.eda.tactics.policy.PolicyTripleVariable;
import itti.com.pl.eda.tactics.policy.TimeCondition;
import itti.com.pl.eda.tactics.policy.impl.PolicyTripleImpl;

/**
 * translates different kinds of policy triple (both actions and conditions)
 * @author marcin
 *
 */
public class TripleTranslator {

	private static PolicyTriple getTriple(PolicyTriple oryginalTriple, TripleType returnType){

		if(oryginalTriple != null && returnType != null){

			PolicyTripleVariable variable = oryginalTriple.getVariableEnum();
			PolicyTripleOperator operator = oryginalTriple.getOperatorEnum();
			String value = oryginalTriple.getValue();

			PolicyTriple triple = null;

			switch (returnType) {
				case TripleImpl:
					triple = new PolicyTripleImpl(variable, operator, value);
					break;
				case HLCondition:
					triple = new PolicyHLCondition(variable, operator, value);
					break;
				case HLAction:
					triple = new PolicyHLAction(variable, operator, value);
					break;
				case ILCondition:
					triple = new PolicyILCondition(variable, operator, value);
					break;
				case ILAction:
					triple = new PolicyILAction(variable, operator, value);
					break;

				default:
					break;
			}

			return triple;

		}else{
			return null;
		}
	}

	/**
	 * translates one triple object to another
	 * @param oryginalTriple
	 * @param returnType implementation of the returning object
	 * @return new triple object
	 */
	public static PolicyTriple getTripleGroup(PolicyTriple oryginalTriple, TripleType returnType){

		if(oryginalTriple != null && returnType != null){

			PolicyTriple groupTriple = null;

			switch (returnType) {

				case TripleImpl:
					groupTriple = new PolicyTripleImpl(LogicalOperator.And);
					break;

				case HLCondition:
					groupTriple = new PolicyHLCondition(LogicalOperator.And);
					break;

				case HLAction:
					groupTriple = new PolicyHLAction(LogicalOperator.And);
					break;

				case ILCondition:
					groupTriple = new PolicyILCondition(LogicalOperator.And);
					break;

				case ILAction:
					groupTriple = new PolicyILAction(LogicalOperator.And);
					break;

				default:
					return null;
			}

			if(oryginalTriple.getElementsLength() > 0){
				for (PolicyTriple element : oryginalTriple.getElements()) {
					PolicyTriple translatedTriple = getTriple(element, returnType);
					if(translatedTriple != null){
						groupTriple.addElement(translatedTriple);
					}
				}
			}
			return groupTriple;

		}else{
			return null;
		}
	}


	public static TimeCondition getTimeConditions(TimeCondition timeConditions){

		if(timeConditions == null){
			return null;
		}else{
			TimeCondition newTimeConditions = new TimeCondition(timeConditions.getLogicalOperatorEnum());

			DateUnit begin = timeConditions.getBegin();
			DateUnit end = timeConditions.getEnd();
			if(begin != null){
				newTimeConditions.setBegin(begin.clone());
			}
			if(end != null){
				newTimeConditions.setEnd(end.clone());
			}

			newTimeConditions.setPeriod(timeConditions.getPeriodEnum());

			if(timeConditions.getConditions() != null){
				for (TimeCondition condition : timeConditions.getConditions()) {
					newTimeConditions.addCondition(getTimeConditions(condition));
				}
			}
			return newTimeConditions;
		}
	}
}
