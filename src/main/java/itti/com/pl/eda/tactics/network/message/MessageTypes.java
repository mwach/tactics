package itti.com.pl.eda.tactics.network.message;

import itti.com.pl.eda.tactics.utils.StringUtils;

public enum MessageTypes {

	/**
	 * used to check if remote pold components are active and able to response
	 */
	Echo,

	/**
	 * returns a high-level policy
	 */
	GetPolicyHL,

	/**
	 * returns selected high-level policies elements (like action of condition elements)
	 */
	GetPolicyHLElements, 

	/**
	 * returns a list of high-level policies names
	 */
	GetPolicyHLNames,


	/**
	 * returns a intermediate-levl policy
	 */
	GetPolicyIL,

	/**
	 * returns a list of intermediate-level policies which adequate policy type specified
	 */
	GetPolicyILByType,

	/**
	 * returns a list of intermediate-level policies which match specified criteria
	 */
	GetPolicyILByCriteria,

	/**
	 * changes state (activates or deactivates) an intermediate-level policy
	 */
	ActivateILPolicy,


	/**
	 * stores a high-level policy in pold repository
	 */
	StoreHLPolicy,

	/**
	 * stores a apm-defined policy in pold repository
	 */
	StoreAPMPolicy,

	/**
	 * deletes a high-level policy (and all related to it objects) from pold repository
	 */
	DeleteHLPolicy,

	/**
	 * replaces a high-level policy with the new one
	 */
	ReplaceHLPolicy, 

	/**
	 * returns pold user id
	 */
	GetUserID, 

	/**
	 * informs, that message contains some instructions sent by pold operator
	 */
	OperatorRequest,

	/**
	 * registers a new pold listener object
	 */
	RegisterListener,

	/**
	 * unregisters one of pold listener objects
	 */
	UnregisterListener, 

	/**
	 * used to inform pold listeners about internal events
	 */
	Notification, 

	/**
	 * used for test purposes
	 */
	TestCase, 

	;

	/**
	 * returns enum value of message type
	 * @param name message type in string form
	 * @return enum value of message type or null if name is invalid
	 */
	public static MessageTypes getMessageType(String name){

		if(StringUtils.isNullOrEmpty(name)){
			return null;

		}else{
			for (MessageTypes messageType : MessageTypes.values()) {
				if(messageType.name().equalsIgnoreCase(name)){
					return messageType;
				}
			}
		}
		return null;
	}
}
