package itti.com.pl.eda.tactics.network.message;

import itti.com.pl.eda.tactics.utils.StringUtils;

/**
 * available argument names for pold messages
 * @author marcin
 *
 */
public enum ArgumentName {

	/**
	 * id of the policy (used for both high-level and intermediate-level policies)
	 */
	PolicyId,

	/**
	 * id of the policy (used for high-level policies)
	 */
	PolicyHLId,

	/**
	 * name of the policy
	 */
	PolicyName,

	/**
	 * type of the policy (swrl rules defined)
	 */
	PolicyType,

	/**
	 * id of the user
	 */
	UserId,

	/**
	 * name of the user
	 */
	UserName,

	/**
	 * state of the policy (true - active, false - don't care about state)
	 */
	State,

	/**
	 * ip address
	 */
	IpAddr,

	/**
	 * port number
	 */
	Port,

	/**
	 * type of the application
	 */
	AppType, 

	/**
	 * pold server response
	 */
	Response, 

	/**
	 * name of the requestor (unique for every pold client)
	 */
	RequesterName,

	/**
	 * operator parameter value (allows identify operator request type)
	 */
	Operator, 

	/**
	 * test command
	 */
	Test, 

	/**
	 * test value
	 */
	TestParam;


	/**
	 * returns enum value of argument name
	 * @param name argument name in string form
	 * @return enum value of argument name or null if name is invalid
	 */
	public static ArgumentName getArgumentName(String name){

		if(StringUtils.isNullOrEmpty(name)){
			return null;

		}else{
			for (ArgumentName argument : ArgumentName.values()) {
				if(argument.name().equalsIgnoreCase(name)){
					return argument;
				}
			}
		}
		return null;
	}
}
