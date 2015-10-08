package itti.com.pl.eda.tactics.network.message;

import itti.com.pl.eda.tactics.utils.StringUtils;

/**
 * parameters available for pold operator
 * these parameters can be used to construct operator messages
 * @author marcin
 *
 */
public enum OperatorParameter {

	/**
	 * value of the name
	 */
	Name,

	/**
	 * value of the location
	 */
	Location, 
	Type;

	/**
	 * returns enum value of the parameter given as a string
	 * @param parameter parameter name in string form
	 * @return enum value of the parameter, or null if parameter has invalid value
	 */
	public static OperatorParameter getOperatorParameter(String parameter){

		if(StringUtils.isNullOrEmpty(parameter)){
			return null;
		}else{
			for (OperatorParameter param : OperatorParameter.values()) {
				if(param.name().equalsIgnoreCase(parameter)){
					return param;
				}
			}
		}
		return null;
	}
}
