package itti.com.pl.eda.tactics.network.message;

import itti.com.pl.eda.tactics.utils.StringUtils;

/**
 * list of allowed message elements (fragments)
 * used especially for messages stored in text xml-like format
 * @author marcin
 *
 */
public enum MessageElements{

	/**
	 * message root object (on the top of every xml-form message)
	 */
	Message,

	/**
	 * type of the message - for available types look into MessageTypes enumeration
	 */
	MessageType,

	/**
	 * indicates start of list of message arguments
	 */
	Arguments,

	/**
	 * single message argument
	 */
	Argument,

	/**
	 * name of the message argument
	 */
	Name,

	/**
	 * value of the message argument
	 */
	Value;


	/**
	 * returns enum value of message element
	 * @param name message element name in string form
	 * @return enum value of message element name or null if name is invalid
	 */
	public static MessageElements getMessageElement(String name){

		if(StringUtils.isNullOrEmpty(name)){
			return null;

		}else{
			for (MessageElements messageElement : MessageElements.values()) {
				if(messageElement.name().equalsIgnoreCase(name)){
					return messageElement;
				}
			}
		}
		return null;
	}
}
