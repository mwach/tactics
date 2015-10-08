package itti.com.pl.eda.tactics.policy;

import itti.com.pl.eda.tactics.utils.StringUtils;

public enum PolicyTripleVariable {

	Application,
	Port,
	IpAddress,
	Time,
	Network,
	Localization,
	Target_localization,

	Quality,

	Jitter,
	Bandwidth,
	Delay,
	Loss,

	Qos_level;

	public static PolicyTripleVariable getValue(String item){

		if(StringUtils.isNullOrEmpty(item)){
			return null;

		}else{

			//TODO: hardcoded - ontology need to be updated
			if(item.equalsIgnoreCase("Throughput")){
				item = Bandwidth.name();
			}

			for (PolicyTripleVariable policyTripleVariable : PolicyTripleVariable.values()) {
				if(policyTripleVariable.name().equalsIgnoreCase(item)){
					return policyTripleVariable;
				}
			}
		}
		return null;
	};
}
