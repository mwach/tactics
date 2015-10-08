package itti.com.pl.eda.tactics.data.model;

import java.util.HashMap;
import java.util.Map;

import itti.com.pl.eda.tactics.exception.QosPropertyException;
import itti.com.pl.eda.tactics.exception.QosPropertyException.QosPropertyExceptionType;
import itti.com.pl.eda.tactics.policy.IQoSLevel;
import itti.com.pl.eda.tactics.policy.PolicyTripleVariable;

/**
 * transporting class used to keep information about single qos level
 * @author marcin
 *
 */
public class QoSLevel implements IQoSLevel{

	private static final long serialVersionUID = 1L;

	private long id = -1;
	private Map<PolicyTripleVariable, QosParameter> qosProperties = 
					new HashMap<PolicyTripleVariable, QosParameter>();


	/**
	 * sets id
	 * @param id
	 */
	public void setId(long id){
		this.id = id;
	}

	/**
	 * returns id
	 * @return id
	 */
	public long getId(){
		return id;
	}

	/**
	 * add new qos parameter to the object
	 * @param qosProperty QoS property
	 * @throws QosPropertyException 
	 */
	public void addQosParameter(QosParameter qosProperty) throws QosPropertyException {

		if(isAllowedProperty(qosProperty)){
			qosProperties.put(qosProperty.getProperty(), qosProperty);

		}else if (qosProperty == null){
			throw new QosPropertyException(QosPropertyExceptionType.PropertyNameIsNull, "Exception in 'addQoSParameter method'");

		}else{
			throw new QosPropertyException(QosPropertyExceptionType.InvalidPropertyName, qosProperty.toString());
		}
	}

	private boolean isAllowedProperty(QosParameter property){

		if(property == null || property.getProperty() == null){
			return false;

		}else{
			PolicyTripleVariable var = property.getProperty();
			if(var != null){

				for (PolicyTripleVariable allowedItem : allowedItems) {
					if(allowedItem == var){
						return true;
					}
				}
			}
			return false;
		}
	}


	/**
	 * returns delay value
	 * @return delay
	 */
	public int getDelay() {
		return getQosParameterValue(PolicyTripleVariable.Delay);
	}

	/**
	 * returns jitter value
	 * @return jitter
	 */
	public int getJitter() {
		return getQosParameterValue(PolicyTripleVariable.Jitter);
	}

	/**
	 * returns bandwidth value
	 * @return bandwidth
	 */
	public int getBandwidth() {
		return getQosParameterValue(PolicyTripleVariable.Bandwidth);
	}

	/**
	 * returns loss value
	 * @return loss
	 */
	public int getLoss() {
		return getQosParameterValue(PolicyTripleVariable.Loss);
	}

	/**
	 * sets delay value
	 * @param delay
	 */
	public void setDelay(int delay) {
		setQosParameterValue(PolicyTripleVariable.Delay, delay);
	}

	/**
	 * sets jiter value
	 * @param jiter
	 */
	public void setJitter(int jitter) {
		setQosParameterValue(PolicyTripleVariable.Jitter, jitter);
	}

	/**
	 * sets bandwidth value
	 * @param bandwidth
	 */
	public void setBandwidth(int bandwidth) {
		setQosParameterValue(PolicyTripleVariable.Bandwidth, bandwidth);
	}

	/**
	 * sets loss value
	 * @param loss
	 */
	public void setLoss(int loss) {
		setQosParameterValue(PolicyTripleVariable.Loss, loss);
	}

	private int getQosParameterValue(PolicyTripleVariable name) {

		if(name != null){
			QosParameter prop = qosProperties.get(name);
			if(prop != null){
				return prop.getValue();
			}else{
				return -1;
			}
		}else{
			return -1;
		}
	}

	private void setQosParameterValue(PolicyTripleVariable name, int value) {

		QosParameter prop = null;

		if(name != null){
			if(qosProperties.containsKey(name)){
				prop = qosProperties.get(name);
			}else{
				prop = new QosParameter(name);
				if(isAllowedProperty(prop)){
					qosProperties.put(name, prop);
				}
			}
			prop.setValue(value);
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id: " + id + "\n");
		for (QosParameter property : qosProperties.values()) {
			sb.append(property.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
