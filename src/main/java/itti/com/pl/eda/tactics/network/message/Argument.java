package itti.com.pl.eda.tactics.network.message;

import java.io.Serializable;

/**
 * element of the pold message
 * keeps information about single message parameter
 * @author marcin
 *
 */
public class Argument implements Serializable{

	private static final long serialVersionUID = 1L;

	private ArgumentName name = null;
	private OperatorParameter operatorArgumentName = null;
	private String value = null;

	/**
	 * constructor 
	 * @param name name of the argument
	 * @param value value of the argument
	 */
	public Argument(ArgumentName name, long value) {
		this.name = name;
		this.value = Long.toString(value);
	}


	/**
	 * constructor 
	 * @param name name of the argument
	 * @param value value of the argument
	 */
	public Argument(ArgumentName name, boolean value) {
		this.name = name;
		this.value = Boolean.toString(value);
	}


	/**
	 * constructor 
	 * @param operatorArgumentName name of the operator argument
	 * @param value value of the argument
	 */
	public Argument(OperatorParameter operatorArgumentName, String value) {
		this.operatorArgumentName = operatorArgumentName;
		this.value = value;
	}


	/**
	 * constructor 
	 * @param name name of the operator argument
	 * @param value value of the argument
	 */
	public Argument(OperatorParameter operatorArgumentName, long value) {
		this.operatorArgumentName = operatorArgumentName;
		this.value = Long.toString(value);
	}


	/**
	 * constructor 
	 * @param name name of the argument
	 * @param value value of the argument
	 */
	public Argument(ArgumentName name, String value) {
		this.name = name;
		this.value = value;
	}


	/**
	 * returns argument name
	 * @return argument name
	 */
	public ArgumentName getName() {
		return name;
	}


	/**
	 * returns operator argument name
	 * @return operator argument name
	 */
	public OperatorParameter getOperatorName() {
		return operatorArgumentName;
	}

	/**
	 * returns value
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return (name == null ? operatorArgumentName : name) + ": " + value;
	}
}
