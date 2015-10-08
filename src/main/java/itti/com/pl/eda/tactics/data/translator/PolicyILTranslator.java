package itti.com.pl.eda.tactics.data.translator;

import java.util.ArrayList;
import java.util.Collection;

import eu.netqos.utils.policies.ConnectionId;
import itti.com.pl.eda.tactics.data.model.PolicyIntermediateLevel;
import itti.com.pl.eda.tactics.data.model.policy.TripleType;
import itti.com.pl.eda.tactics.policy.ILPolicy;
import itti.com.pl.eda.tactics.policy.PolicyTriple;
import itti.com.pl.eda.tactics.policy.PolicyTripleOperator;
import itti.com.pl.eda.tactics.policy.PolicyTripleVariable;
import itti.com.pl.eda.tactics.policy.TimeCondition;
import itti.com.pl.eda.tactics.policy.impl.ILPolicyImpl;
import itti.com.pl.eda.tactics.policy.impl.PolicyTripleImpl;


/**
 * translates pold server-side intermediate-level policies into client-side objects and vice-versa
 * @author marcin
 *
 */
public class PolicyILTranslator {

	/**
	 * translates server-side policy into client-side once
	 * @param policy server-side policy
	 * @return client-side policy
	 */
	public static ILPolicy getPolicyILBean(PolicyIntermediateLevel policy){

		if(policy != null){

			ILPolicyImpl bean = new ILPolicyImpl();
			bean.setId(policy.getId());
			bean.setWeight(policy.getWeight());

			bean.setQoSLevel(QoSLevelTranslator.getQoSLevel(policy.getQosLevel()));

			PolicyTriple conditions = TripleTranslator.getTripleGroup(policy.getConditions(), TripleType.TripleImpl);

			if(policy.getIpAddress() != null){
				conditions.addElement(
						new PolicyTripleImpl(PolicyTripleVariable.IpAddress, PolicyTripleOperator.Equals, policy.getIpAddress()));
			}

			if(policy.getApplication() != null){
				conditions.addElement(
						new PolicyTripleImpl(PolicyTripleVariable.Application, PolicyTripleOperator.Equals, policy.getApplication()));
			}

			bean.setConditions(conditions);

			PolicyTriple actions = TripleTranslator.getTripleGroup(policy.getActions(), TripleType.TripleImpl);
			bean.setActions(actions);

			TimeCondition timeCondition = TripleTranslator.
					getTimeConditions(policy.getTimeConditions());
			bean.setTimeConditions(timeCondition);

			if(policy.getAuthor() != null){
				bean.setAuthorName(policy.getAuthor().getName());
				bean.setAuthorId(policy.getAuthor().getId());
			}

			ConnectionId connId = new ConnectionId();
			connId.setId(String.valueOf(policy.getId()));
			bean.setConnectionId(connId);

			return bean;

		}else{
			return null;
		}
	}


	/**
	 * translates the whole collection of server-side intermediate-level policies into collection of client-side policies
	 * @param policiesCollection server-side collection
	 * @return client-side collection
	 */
	public static Collection<ILPolicy> getPoliciesILBeans(
			Collection<PolicyIntermediateLevel> policiesCollection) {

		if(policiesCollection != null){
			Collection<ILPolicy> retCollection = new ArrayList<ILPolicy>();
			for (PolicyIntermediateLevel policy : policiesCollection) {
				ILPolicy bean = getPolicyILBean(policy);
				if(bean != null){
					retCollection.add(bean);
				}
			}
			return retCollection;

		}else{
			return null;
		}
	}
}
