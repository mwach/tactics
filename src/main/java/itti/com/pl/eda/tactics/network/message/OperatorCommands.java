package itti.com.pl.eda.tactics.network.message;

import itti.com.pl.eda.tactics.utils.StringUtils;

/**
 * available commands for pold operator
 * @author marcin
 *
 */
public enum OperatorCommands {

	/**
	 * command used when operator wants to add new pold user
	 */
	AddUser,

	/**
	 * command used when operator wants to load pold user data
	 */
	LoadUser,

	/**
	 * command used when operator wants to deletes pold user from database
	 */
	DeleteUser,

	/**
	 * command used when operator wants to retrieve a list of all pold users
	 */
	UsersList,

	/**
	 * command used when operator wants to add new pold user
	 */
	UpdateUser,

	/**
	 * command used when operator wants to add new location
	 */
	AddLocation,

	/**
	 * command used when operator wants to retrieve a list of locations
	 */
	LocationsList,

	/**
	 * command used when operator wants to link location with pold user
	 */
	AddLocationToTheUser,

	/**
	 * command used when operator wants to remove link between location and the pold user
	 */
	RemoveLocationFromTheUser,

	/**
	 * command used when operator wants to load location data
	 */
	LoadLocation,

	/**
	 * command used when delete a location from pold database
	 */
	DeleteLocation,

	/**
	 * command used when operator wants to retrieve a list of networks
	 */
	NetworksList, 
	AddNetwork, 
	RemoveNetwork, 
	UpdateLocation, 
	AddApplication, 
	LoadNetwork, 
	UpdateNetwork, 
	ApplicationList, 
	DeleteApplication, 
	LoadApplication, 
	UpdateApplication,

	/**
	 * forces POLD to run SWRL rules engine
	 */
	SetRefinementEngine,
	RunSqrlEngine,

	/**
	 * qos parameters for policies
	 */
	AddQoSPolicy,
	QoSPoliciesList, 
	DeleteQoSPolicy, 
	LoadQoSPolicy, 
	UpdateQoSPolicy,

	;

	/**
	 * parses operator command from string form into enum value
	 * @param command command in string form
	 * @return enum value, or null if parsing failed
	 */
	public static OperatorCommands getOperatorCommand(String command){

		if(StringUtils.isNullOrEmpty(command)){
			return null;
		}

		for (OperatorCommands operator : OperatorCommands.values()) {
			if(operator.name().equalsIgnoreCase(command)){
				return operator;
			}
		}
		return null;
	}
}
