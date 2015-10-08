package itti.com.pl.eda.tactics.data.model.policy;

import itti.com.pl.eda.tactics.utils.StringUtils;

public enum TripleType{ 
	TripleImpl, 
	HLCondition, 
	HLAction, 
	ILCondition, 
	ILAction;

	public static TripleType getTripleType(String value){

		if(StringUtils.isNullOrEmpty(value)){
			return null;

		}else{
			for (TripleType tripleType : TripleType.values()) {
				if(tripleType.name().equalsIgnoreCase(value)){
					return tripleType;
				}
			}
		}
		return null;
	}
};