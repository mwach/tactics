package itti.com.pl.eda.tactics.data.model.policy;

import itti.com.pl.eda.tactics.policy.LogicalOperator;
import itti.com.pl.eda.tactics.policy.PolicyTriple;
import itti.com.pl.eda.tactics.policy.PolicyTripleOperator;
import itti.com.pl.eda.tactics.policy.PolicyTripleVariable;

/**
 * implementation of PolicyTriple class
 * used for intermediate-level policy conditions
 * @author marcin
 *
 */
public class PolicyILCondition extends PolicyTriple{

	private static final long serialVersionUID = 1L;

	/**
	 * default constructor
	 * @param variable
	 * @param operator
	 * @param value
	 */
	public PolicyILCondition(PolicyTripleVariable variable, PolicyTripleOperator operator, String value) {
		super(variable, operator, value);
	}

	@SuppressWarnings("unused")
	private PolicyILCondition(){
		super();
	}

	/**
	 * constructor used for hierarchical triples
	 * @param operator
	 */
	public PolicyILCondition(LogicalOperator operator){
		super(operator);
	}

	@Override
	public final char getPolicyType() {
		return 'I';
	}

	@Override
	public final char getSection() {
		return 'C';
	}
}
