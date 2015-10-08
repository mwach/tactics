package itti.com.pl.eda.tactics.data.model;

import java.io.Serializable;

import itti.com.pl.eda.tactics.policy.PolicyTripleVariable;

/**
 * transporting class used to keep information about single qos parameter like jitter or delay
 * @author marcin
 *
 */
public class QosParameter implements Serializable{

	private static final long serialVersionUID = 1L;

	private PolicyTripleVariable property = null;

	private int value = -1;
	private int variation = -1;

	/**
	 * default constructor
	 * @param property name of the qos property
	 */
	public QosParameter(PolicyTripleVariable property){
		this.property = property;
	}

	/**
	 * returns property name
	 * @return property
	 */
	public PolicyTripleVariable getProperty(){
		return property;
	}

	/**
	 * sets property value
	 * @param value
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * returns property value
	 * @return value
	 */
	public int getValue(){
		return value;
	}

	/**
	 * sets property variation
	 * @param variation
	 */
	public void setVariation(int variation) {
		this.variation = variation;
	}

	/**
	 * returns property variation
	 * @return value of the variation
	 */
	public int getVariation(){
		return variation;
	}

	public String toString() {
		return "Property: " + property + ", value: " + value + ", variation: " + variation;
	}
}