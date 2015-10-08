package itti.com.pl.eda.tactics.exception;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * class defines common pold exception behaviours 
 * like error message creation process
 * all pold exception classes must extend this one
 * @author marcin
 *
 */
public abstract class AbstractPoldException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * each of extending classes must have its own enum list, that will specify
	 * possible error codes, every error code should have meaningful description
	 * (error list should be finite, enumerated list to avoid some errors) 
	 * example: 
	 * 		public enum ERR_LIST {ERR1, ERR2 };
	 * 		descriptions.put(ERR1.name(), "Some description"
	 */
	protected static Map<String, String> descriptions = new HashMap<String, String>();

	/**
	 * default exception constructor
	 * @param exceptionDescription name of selected enum position
	 * @param desc additional (optional) description e. g. method name
	 */
	public AbstractPoldException(String exceptionDescription, String desc) {
		super(getExceptionDescription(exceptionDescription, desc));
	}

	/**
	 * second constructor
	 * as a second argument a list of errors can be put
	 * @param exceptionDescription name of selected enum position
	 * @param errList list of errors
	 */
	public AbstractPoldException(String exceptionDescription, Collection<String> errList) {
		super(getExceptionDescription(exceptionDescription, getMessageFromList(errList)));
	}

	/**
	 * third constructor
	 * @param exceptionDescription
	 */
	public AbstractPoldException(String exceptionDescription) {
		super(getExceptionDescription(exceptionDescription, null));
	}

	/**
	 * forms a message from list of errors
	 * @param errList list of errors
	 * @return string message that is next used as an exception constructor
	 */
	private final static String getMessageFromList(Collection<String> errList) {
		StringBuffer sb = new StringBuffer();
		if(errList != null && !errList.isEmpty()){
			for (String msg : errList) {
				sb.append("\t" + msg);
				sb.append(msg);
				sb.append('\n');
			}
		}
		return sb.toString();
	}

	/**
	 * creates error descriptor
	 * @param type error type
	 * @param msg info about error
	 * @return exception description
	 */
	private static String getExceptionDescription(String type, String msg){

		StringBuffer sb = new StringBuffer();

		if(type != null){
			String description = descriptions.get(type);
			if(description == null){
				description = type;
			}
			sb.append(type + ": ");
			sb.append(description);
		}
		if(msg != null){
			sb.append(":" + msg);
		}

		return sb.toString();
	}
}
