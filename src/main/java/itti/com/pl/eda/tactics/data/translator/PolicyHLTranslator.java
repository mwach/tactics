package itti.com.pl.eda.tactics.data.translator;

import java.util.ArrayList;
import java.util.Collection;

import itti.com.pl.eda.tactics.data.model.PolicyHighLevel;
import itti.com.pl.eda.tactics.data.model.policy.TripleType;
import itti.com.pl.eda.tactics.policy.HLPolicy;
import itti.com.pl.eda.tactics.policy.PolicyTriple;
import itti.com.pl.eda.tactics.policy.TimeCondition;
import itti.com.pl.eda.tactics.policy.impl.HLPolicyImpl;

/**
 * translates pold high-level policy into client policy representation and vice-versa
 * @author marcin
 *
 */
public class PolicyHLTranslator {

	/**
	 * returns client-side version of high-level policy
	 * @param policy pold server-side policy
	 * @return client-side policy
	 */
	public static HLPolicy getPolicyHLBean(PolicyHighLevel policy){

		if(policy != null){

			HLPolicyImpl policyBean = new HLPolicyImpl();
			policyBean.setId(policy.getId());
			policyBean.setName(policy.getName());

			if(policy.getAuthor() != null){
				policyBean.setAuthor(policy.getAuthor().getName());
			}

			PolicyTriple conditions = TripleTranslator.getTripleGroup(policy.getConditions(), TripleType.TripleImpl);
			policyBean.setConditions(conditions);

			PolicyTriple actions = TripleTranslator.getTripleGroup(policy.getActions(), TripleType.TripleImpl);
			policyBean.setActions(actions);

			TimeCondition timeConditions = TripleTranslator.getTimeConditions(policy.getTimeConditions());
			policyBean.setTimeConditions(timeConditions);

			return policyBean;

		}else{
			return null;
		}
	}


	/**
	 * returns server-side version of high-level policy
	 * @param hlPolicy client-side policy
	 * @return server-side policy
	 */
	public static PolicyHighLevel getPolicyHighLevel(HLPolicy hlPolicy){

		if(hlPolicy != null){

			PolicyHighLevel policy = new PolicyHighLevel();
			policy.setId(hlPolicy.getId());
			policy.setName(hlPolicy.getName());

			policy.setActions(TripleTranslator.getTripleGroup(hlPolicy.getActions(), TripleType.HLAction));
			policy.setConditions(TripleTranslator.getTripleGroup(hlPolicy.getConditions(), TripleType.HLCondition));

			TimeCondition timeConditions = TripleTranslator.getTimeConditions(hlPolicy.getTimeConditions());
			policy.setTimeConditions(timeConditions);

			return policy;

		}else{
			return null;
		}
	}


	/**
	 * translates a collection of server-side hl policies at once
	 * @param policies collection of server-side policies
	 * @return collection of client-side policies
	 */
	public static Collection<HLPolicy> getPoliciesHLBeans(Collection<PolicyHighLevel> policies){

		if(policies != null){
			Collection<HLPolicy> retList = new ArrayList<HLPolicy>();
			for (PolicyHighLevel policy : policies) {
				HLPolicy bean = getPolicyHLBean(policy);
				if(bean != null){
					retList.add(bean);
				}
			}
			return retList;

		}else{
			return null;
		}
	}


}
