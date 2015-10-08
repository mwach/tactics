package itti.com.pl.eda.tactics.data.model.policy;

import itti.com.pl.eda.tactics.policy.LogicalOperator;
import itti.com.pl.eda.tactics.policy.PolicyTriple;
import itti.com.pl.eda.tactics.policy.PolicyTripleOperator;
import itti.com.pl.eda.tactics.policy.PolicyTripleVariable;

/**
 * implementation of PolicyTriple class
 * used for high-level policy conditions
 * @author marcin
 *
 */

public class PolicyHLCondition extends PolicyTriple{

	private static final long serialVersionUID = 1L;

	/**
	 * default constructor
	 * @param var
	 * @param oper
	 * @param val
	 */
	public PolicyHLCondition(PolicyTripleVariable var, PolicyTripleOperator oper, String val) {
		super(var, oper, val);
	}

	@SuppressWarnings("unused")
	private PolicyHLCondition(){
		super();
	}

	/**
	 * constructor used for hierarchical triples
	 * @param operator
	 */
	public PolicyHLCondition(LogicalOperator operator){
		super(operator);
	}

	@Override
	public final char getPolicyType() {
		return 'H';
	}

	@Override
	public final char getSection() {
		return 'C';
	}
}
