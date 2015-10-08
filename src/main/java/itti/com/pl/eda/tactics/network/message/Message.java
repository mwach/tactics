package itti.com.pl.eda.tactics.network.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import itti.com.pl.eda.tactics.exception.MessageException;
import itti.com.pl.eda.tactics.exception.MessageException.MessageExceptionType;
import itti.com.pl.eda.tactics.utils.PoldSettings;
import itti.com.pl.eda.tactics.utils.StringUtils;


/**
 * main class for messages creating/parsing
 * message objects are xml'like messages sent between pold elements through the network
 * @author marcin
 *
 */
public class Message implements Serializable{

	private static final long serialVersionUID = 1L;

	private MessageTypes messageType;
	private List<Argument> arguments = new ArrayList<Argument>();


	/**
	 * default constructor
	 * @param messageType type of the message
	 */
	public Message(MessageTypes messageType){
		this.messageType = messageType;
	}


	/**
	 * adds argument to the message
	 * @param argument argument object
	 */
	public void addArgument(Argument argument){
		this.arguments.add(argument);
	}


	/**
	 * returns message in xml-like format
	 * used to format message into proper form before sending through the network
	 * @return message in text xml-like format
	 */
	public String getFormattedMessage(){

		if(messageType == null){
			return null;
		}

		StringBuffer sb = new StringBuffer();

		sb.append(getStartClosedTag(MessageElements.Message));

		sb.append(getStartClosedTag(MessageElements.MessageType));
		sb.append(messageType);
		sb.append(getEndClosedTag(MessageElements.MessageType));

		sb.append(getStartClosedTag(MessageElements.Arguments));

		for (Argument argument : arguments) {
			String name = null;
			if(argument.getName() != null){
				name = argument.getName().toString();
			}else{
				name =  PoldSettings.OperatorPrefix + argument.getOperatorName().toString();
			}
			String value = argument.getValue();

			sb.append(getStartOpenTag(MessageElements.Argument));
			sb.append(getAttribute(MessageElements.Name, name));
			sb.append(getAttribute(MessageElements.Value, value));
			sb.append(getEndOpenTag());
		}

		sb.append(getEndClosedTag(MessageElements.Arguments));

		sb.append(getEndClosedTag(MessageElements.Message));

		return sb.toString();
	}


	private String getAttribute(MessageElements arg, String val) {
		return ' ' + arg.toString() + "=\"" + val + "\"";
	}


	/**
	 * parses message stored in text xml-like form into message object
	 * @param object message in text xml-like form 
	 * @return message object
	 * @throws MessageException if there were errors during parsing
	 */
	public static Message parseMessage(Object object) throws MessageException {

		if(object == null){
			throw new MessageException(MessageExceptionType.MessageIsNull);
		}else{
			return parseMessage(object.toString());
		}
	}


	private static Message parseMessage(String inputLine) throws MessageException {

		if(inputLine == null || inputLine.trim().equals("")){
			throw new MessageException(MessageExceptionType.MessageIsEmpty);
		}

		Message message = null;

		String type = getMessageFragment(inputLine, MessageElements.MessageType);
		MessageTypes messageType = MessageTypes.getMessageType(type);

		if(messageType == null){
			throw new MessageException(MessageExceptionType.MessageHasInvalidType, type);
		}

		message = new Message(messageType);

		String argumentPart = getMessageFragment(inputLine, MessageElements.Arguments);
		List<Argument> arguments = createArgumentsList(argumentPart);
		if(arguments != null){
			for (Argument argument : arguments) {
				message.addArgument(argument);
			}
		}
		return message;
	}



	private static List<Argument> createArgumentsList(String argumentPart) throws MessageException{

		String[] argsList = argumentPart.split(getStartOpenTag(MessageElements.Argument));
		List<Argument> arguments = new ArrayList<Argument>();

		for (String argumentString : argsList) {
			argumentString = argumentString.trim();
			if(argumentString.length() > 0){

				String argumentName = getArgument(argumentString, MessageElements.Name);
				String argumentValue = getArgument(argumentString, MessageElements.Value);

				Argument argument = null;

				if(argumentName.startsWith(PoldSettings.OperatorPrefix)){

					argumentName = argumentName.substring(PoldSettings.OperatorPrefix.length());

					OperatorParameter operatorParam = OperatorParameter.getOperatorParameter(argumentName);
					if(operatorParam != null){
						argument = new Argument(operatorParam,	argumentValue);
					}else{
						throw new MessageException(MessageExceptionType.MessageHasInvalidOperatorParameter, argumentName);
					}

				}else{

					ArgumentName argName = ArgumentName.getArgumentName(argumentName);
					if(argName != null){
						argument = new Argument(argName, argumentValue);
					}else{
						throw new MessageException(MessageExceptionType.MessageHasInvalidArgument, argumentName);
					}
				}

				if(argument != null){
					arguments.add(argument);
				}
			}
		}
		return arguments;
	}


	private static String getArgument(String argumentString, MessageElements element) throws MessageException{

		int argStart = argumentString.indexOf(element.name());
		if(argStart == -1){
			throw new MessageException(
					MessageExceptionType.MessageHasInvalidArgument, element.name() + " : " + argumentString);
		}

		argStart += element.name().length() + 2;

		int argEnd = argumentString.indexOf("\"", argStart);

		if(argEnd != -1 && argEnd == argStart){
			return null;

		}else if(argEnd == -1 || argEnd <= argStart){
			throw new MessageException(
					MessageExceptionType.MessageHasInvalidArgument, element.name() + " : " + argumentString);
		}

		return argumentString.substring(argStart, argEnd);
	}


	private static String getMessageFragment(String inputLine, MessageElements msgFragment) throws MessageException{

		String fragmentStart = getStartClosedTag(msgFragment);
		String fragmentEnd = getEndClosedTag(msgFragment);

		int begin = inputLine.indexOf(fragmentStart);
		int end = inputLine.indexOf(fragmentEnd);

		if(begin == -1 || end == -1 || begin >= end || end >= inputLine.length()){
			throw new MessageException(MessageExceptionType.MessageHasWrongFormat, inputLine);
		}

		begin += fragmentStart.length();

		String data = inputLine.substring(begin, end);
		return data;
	}


	private static String getStartClosedTag(MessageElements msgFragment){
		return getStartOpenTag(msgFragment) + '>';
	}


	private static String getStartOpenTag(MessageElements msgFragment){
		return '<' + msgFragment.toString();
	}


	private static String getEndOpenTag(){
		return " />";
	}


	private static String getEndClosedTag(MessageElements msgFragment){
		return "</" + msgFragment.toString() + '>';
	}


	/**
	 * returns message type
	 * @return messageType
	 */
	public MessageTypes getMessageType() {
		return messageType;
	}


	/**
	 * returns value of the argument
	 * @param argName name of the argument
	 * @return value of the argument or null if argument doensn't exist in message
	 */
	public String getArgumentValue(ArgumentName argName) {

		for (Argument arg : arguments) {
			if(arg.getName().equals(argName)){
				return arg.getValue();
			}
		}
		return null;
	}


	/**
	 * returns value of the argument as long
	 * @param argName name of the argument
	 * @return long value of the argument or -1 if argument doensn't exist in message
	 * @throws MessageException
	 */
	public long getArgumentValueLong(ArgumentName argName) throws MessageException {

		String value = getArgumentValue(argName);

		if(value != null){
			int valueInt = StringUtils.getIntValue(value);
			return valueInt;

		}else{
			return -1;
		}
	}


	/**
	 * 
	 * returns value of the argument as boolean
	 * @param argName name of the argument
	 * @return boolean value of the argument or false if argument doensn't exist in message
	 * @throws MessageException
	 */
	public boolean getArgumentValueBoolean(ArgumentName argName) throws MessageException {

		String value = getArgumentValue(argName);

		if(value != null){

			boolean valueBool = StringUtils.getBooleanValue(value);
			return valueBool;

		}else{
			return false;
		}
	}


	/**
	 * returns a map of all operator arguments
	 * @return map of all operator arguments or null if message doesn't contain operator arguments
	 */
	public Map<OperatorParameter, String> getOperatorArgumentsMap(){

		if(arguments != null){
			Map<OperatorParameter, String> map = new HashMap<OperatorParameter, String>();
			for (Argument arg : arguments) {
				if(arg.getOperatorName() != null){
					map.put(arg.getOperatorName(), arg.getValue());
				}
			}
			return map;
		}
		return null;
	}


	@Override
	public String toString() {
		return getFormattedMessage();
	}
}
