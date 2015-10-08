package itti.com.pl.eda.tactics.data.translator;

import itti.com.pl.eda.tactics.data.model.PolicyQoSLevel;

public class PolicyTranslator {

	public static PolicyQoSLevel getPolicyQoSLevel(
			itti.com.pl.eda.tactics.operator.PolicyQoSLevel policy) {

		if(policy != null){
			PolicyQoSLevel newPolicy = new PolicyQoSLevel();
			newPolicy.setName(policy.getName());

			newPolicy.setBandwidth(policy.getBandwidth());
			newPolicy.setDelay(policy.getDelay());
			newPolicy.setJitter(policy.getJitter());
			newPolicy.setLoss(policy.getLoss());

			return newPolicy;
		}
		return null;
	}

	public static itti.com.pl.eda.tactics.operator.PolicyQoSLevel getPolicyQoSLevel(
			PolicyQoSLevel policy) {

		if(policy != null){
			itti.com.pl.eda.tactics.operator.PolicyQoSLevel newPolicy = new itti.com.pl.eda.tactics.operator.PolicyQoSLevel();
			newPolicy.setName(policy.getName());

			newPolicy.setBandwidth(policy.getBandwidth());
			newPolicy.setDelay(policy.getDelay());
			newPolicy.setJitter(policy.getJitter());
			newPolicy.setLoss(policy.getLoss());

			return newPolicy;
		}
		return null;
	}

}
