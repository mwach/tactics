package itti.com.pl.eda.tactics.utils;

public enum RefinementMethods {

	JavaBased,
	SwrlBased,
	;




	public static RefinementMethods getValue(String parameter) {

		if(StringUtils.isNullOrEmpty(parameter)){
			return null;
		}
		for (RefinementMethods method : RefinementMethods.values()) {
			if(method.name().equals(parameter)){
				return method;
			}
		}
		return null;
	};
}
