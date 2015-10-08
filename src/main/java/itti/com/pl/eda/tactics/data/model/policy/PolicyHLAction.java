package itti.com.pl.eda.tactics.data.model.policy;

import itti.com.pl.eda.tactics.policy.LogicalOperator;
import itti.com.pl.eda.tactics.policy.PolicyTriple;
import itti.com.pl.eda.tactics.policy.PolicyTripleOperator;
import itti.com.pl.eda.tactics.policy.PolicyTripleVariable;

/**
 * implementation of PolicyTriple class
 * used for high-level policy actions
 * @author marcin
 *
 */
public class PolicyHLAction extends PolicyTriple{

	private static final long serialVersionUID = 1L;

	/**
	 * default constructor
	 * @param variable
	 * @param operator
	 * @param value
	 */
	public PolicyHLAction(PolicyTripleVariable variable, PolicyTripleOperator operator, String value) {
		super(variable, operator, value);
	}

	@SuppressWarnings("unused")
	private PolicyHLAction(){
		super();
	}

	/**
	 * constructor used for hierarchical triples
	 * @param operator
	 */
	public PolicyHLAction(LogicalOperator operator){
		super(operator);
	}

	@Override
	public final char getPolicyType() {
		return 'H';
	}

	@Override
	public final char getSection() {
		return 'A';
	}
}
