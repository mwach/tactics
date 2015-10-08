package itti.com.pl.eda.tactics.policy.impl;

import java.util.Map;

import itti.com.pl.eda.tactics.policy.LogicalOperator;
import itti.com.pl.eda.tactics.policy.PolicyTriple;
import itti.com.pl.eda.tactics.policy.PolicyTripleOperator;
import itti.com.pl.eda.tactics.policy.PolicyTripleVariable;
import itti.com.pl.eda.tactics.utils.StringUtils;

public class PolicyTripleImpl extends PolicyTriple{

	private static final long serialVersionUID = 1L;

	public PolicyTripleImpl(){
		super();
	}

	public PolicyTripleImpl(LogicalOperator operator){
		super(operator);
	}

	public PolicyTripleImpl(PolicyTripleVariable condVar, PolicyTripleOperator condOper, String condVal) {
		super(condVar, condOper, condVal);
	}

	@Override
	public char getPolicyType() {
		return 0;
	}

	@Override
	public char getSection() {
		return 0;
	}

	public String getSqlQuery(Map<String, String> arguments) {

		String qosQuery = "";
		String query = "FROM PolicyIntermediateLevel where ";
		boolean hasCriteria = false;
		boolean hasQoSCriteria = false;
		int i=0;

		String logicalOperator = getLogicalOperator();

		for (PolicyTriple element : getElements()) {

			PolicyTripleVariable var = element.getVariableEnum();
			String varArg = StringUtils.getValidPojoForm(var.name());
			PolicyTripleOperator oper = element.getOperatorEnum();
			String operator = oper.getLogicalSign();

			String val = element.getValue();
			i++;

			if(isQoSParamter(var)){
				if(hasQoSCriteria){
					qosQuery += " " + logicalOperator + " ";
				}
				qosQuery += varArg + operator + ":" + (varArg + i);
				arguments.put(varArg + i, val);
				hasQoSCriteria = true;
			}else{
				if(hasCriteria){
					query  += " " + logicalOperator + " ";
				}
				query += varArg + operator + ":" + (varArg + i);
				arguments.put(varArg + i, val);
				hasCriteria = true;
			}
		}
		if(hasCriteria && hasQoSCriteria){
			query += " " + logicalOperator + " ";
		}
		if(hasQoSCriteria){
			query += " qosLevel in (FROM QoSLevel where " + qosQuery + ")";
		}
		return query;
	}

	private boolean isQoSParamter(PolicyTripleVariable var) {
		if(var != null){
			return (
					var == PolicyTripleVariable.Bandwidth || 
					var == PolicyTripleVariable.Delay ||
					var == PolicyTripleVariable.Jitter ||
					var == PolicyTripleVariable.Loss
			);
		}else{
			return false;
		}
	}

}
