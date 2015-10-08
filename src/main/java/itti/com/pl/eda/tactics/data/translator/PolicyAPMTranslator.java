package itti.com.pl.eda.tactics.data.translator;

import java.util.ArrayList;
import java.util.List;

import eu.netqos.apm.policy.IntermediateCommonServicePolicy;
import itti.com.pl.eda.tactics.data.model.Ontology;
import itti.com.pl.eda.tactics.data.model.PolicyHighLevel;
import itti.com.pl.eda.tactics.data.model.PolicyIntermediateLevel;
import itti.com.pl.eda.tactics.data.model.QoSLevel;
import itti.com.pl.eda.tactics.data.model.User;
import itti.com.pl.eda.tactics.data.model.policy.PolicyHLCondition;
import itti.com.pl.eda.tactics.policy.LogicalOperator;
import itti.com.pl.eda.tactics.policy.PolicyTriple;
import itti.com.pl.eda.tactics.policy.PolicyTripleOperator;
import itti.com.pl.eda.tactics.policy.PolicyTripleVariable;
import itti.com.pl.eda.tactics.utils.StringUtils;

/**
 * translates APM policy into POLD acceptable form
 * @author marcin
 *
 */
public class PolicyAPMTranslator {

	/**
	 * translates APM policy into intermediate-level policy
	 * @param policyApm
	 * @return intermediatePolicy
	 */
	public static PolicyIntermediateLevel getPolicyIntermediateLevel(IntermediateCommonServicePolicy policyApm, User user, Ontology ontology){

		if(policyApm != null){
			//policy high-level object (the main 'container')
			PolicyHighLevel phl = new PolicyHighLevel();
			phl.setDescription(policyApm.getDescription());

			//policy intermediate-level (contains qos parameters)
			PolicyIntermediateLevel pil = new PolicyIntermediateLevel();
			pil.setParent(phl);
			List<PolicyIntermediateLevel> childrenList = new ArrayList<PolicyIntermediateLevel>();
			childrenList.add(pil);
			phl.setChildren(childrenList);

			phl.setAuthor(user);
			pil.setAuthor(user);

			phl.setOntology(ontology);

			//application
			String application = policyApm.getApplication();
			if(application != null){

				PolicyTriple condApp = new PolicyHLCondition(LogicalOperator.And);
				condApp.addElement(new PolicyHLCondition(PolicyTripleVariable.Application, PolicyTripleOperator.Equals, policyApm.getApplication()));
				phl.setConditions(condApp);
				pil.setConditions(condApp);

				pil.setApplication(policyApm.getApplication());
			}

			//ipAddress
			String ipAddress = policyApm.getIpAddress();
			if(ipAddress != null){
				phl.getConditions().addElement(new PolicyHLCondition(PolicyTripleVariable.IpAddress, PolicyTripleOperator.Equals, ipAddress));
				pil.setIpAddress(ipAddress);
			}

			//qos level
			QoSLevel qosLevel = new QoSLevel();
				qosLevel.setBandwidth(StringUtils.getIntValue(policyApm.getBandwidth()));
				qosLevel.setDelay(StringUtils.getIntValue(policyApm.getDelay()));
				qosLevel.setJitter(StringUtils.getIntValue(policyApm.getJitter()));
				qosLevel.setLoss(StringUtils.getIntValue(policyApm.getLoss()));
			pil.setQosLevel(qosLevel);

			return pil;

		}else{
			return null;
		}
	}
}
