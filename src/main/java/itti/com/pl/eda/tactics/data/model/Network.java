package itti.com.pl.eda.tactics.data.model;

import itti.com.pl.eda.tactics.policy.Condition;
import itti.com.pl.eda.tactics.policy.PolicyTripleOperator;
import itti.com.pl.eda.tactics.policy.PolicyTripleVariable;

/**
 * transporting class used to keep information about single network
 * @author marcin
 *
 */
public class Network {

	private long id = -1;
	private String name = null;

	private int delay = -1;
	private int jitter = -1;
	private int bandwidth = -1;
	private int loss = -1;

	private Condition condBandwidth = null;
	private Condition condDelay = null;
	private Condition condJitter = null;
	private Condition condLoss = null;

	/**
	 * returns network id
	 * @return id
	 */
	public long getId() {
		return id;
	}

	/**
	 * sets network id
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * returns network name
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * sets network name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * returns delay value
	 * @return delay
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * returns jitter value
	 * @return jitter
	 */
	public int getJitter() {
		return jitter;
	}

	/**
	 * returns bandwidth value
	 * @return bandwidth
	 */
	public int getBandwidth() {
		return bandwidth;
	}

	/**
	 * returns loss value
	 * @return loss
	 */
	public int getLoss() {
		return loss;
	}

	/**
	 * sets delay value
	 * @param delay
	 */
	public void setDelay(int delay) {
		this.delay = delay;
	}

	/**
	 * sets jiter value
	 * @param jitter
	 */
	public void setJitter(int jitter) {
		this.jitter = jitter;
	}

	/**
	 * sets bandwidth value
	 * @param bandwidth
	 */
	public void setBandwidth(int bandwidth) {
		this.bandwidth = bandwidth;
	}

	/**
	 * sets loss value
	 * @param loss
	 */
	public void setLoss(int loss) {
		this.loss = loss;
	}


	@Override
	public String toString() {
		return "id: " + id + ", name: " + name;
	}

	public Condition getCondBandwidth() {
		return condBandwidth;
	}

	public void setCondBandwidth(Condition condBandwidth) {
		this.condBandwidth = condBandwidth;
	}

	public Condition getCondDelay() {
		return condDelay;
	}

	public void setCondDelay(Condition condDelay) {
		this.condDelay = condDelay;
	}

	public Condition getCondJitter() {
		return condJitter;
	}

	public void setCondJitter(Condition condJitter) {
		this.condJitter = condJitter;
	}

	public Condition getCondLoss() {
		return condLoss;
	}

	public void setCondLoss(Condition condLoss) {
		this.condLoss = condLoss;
	}


	@SuppressWarnings("unused")
	private String getCndBandwidth(){
		return condBandwidth.getFormValue();
	}
	@SuppressWarnings("unused")
	private String getCndDelay(){
		return condDelay.getFormValue();
	}
	@SuppressWarnings("unused")
	private String getCndJitter(){
		return condJitter.getFormValue();
	}
	@SuppressWarnings("unused")
	private String getCndLoss(){
		return condLoss.getFormValue();
	}
	@SuppressWarnings("unused")
	private void setCndBandwidth(String value){
		condBandwidth = Condition.getCondition(value);
	}
	@SuppressWarnings("unused")
	private void setCndDelay(String value){
		condDelay = Condition.getCondition(value);
	}
	@SuppressWarnings("unused")
	private void setCndJitter(String value){
		condJitter = Condition.getCondition(value);
	}
	@SuppressWarnings("unused")
	private void setCndLoss(String value){
		condLoss = Condition.getCondition(value);
	}

	public boolean matchesCriteria(QoSLevel qosLevel,
			PolicyTripleOperator bandwidthOperator,
			PolicyTripleOperator delayOperator,
			PolicyTripleOperator jitterOperator,
			PolicyTripleOperator lossOperator) {

		boolean[] matches = new boolean[4];
		matches[0] = isMatching(getBandwidth(), qosLevel.getBandwidth(), bandwidthOperator, PolicyTripleVariable.Bandwidth);
		matches[1] = isMatching(getDelay(), qosLevel.getDelay(), delayOperator, PolicyTripleVariable.Delay);
		matches[2] = isMatching(getJitter(), qosLevel.getJitter(), jitterOperator, PolicyTripleVariable.Jitter);
		matches[3] = isMatching(getLoss(), qosLevel.getLoss(), lossOperator, PolicyTripleVariable.Loss);
		return matches[0] && matches[1] && matches[2] && matches[3];
	}

	private boolean isMatching(int netValue, int qosValue,
			PolicyTripleOperator operator, PolicyTripleVariable qosParameter) {

		if(netValue > 0 && qosValue > 0){
			if(qosParameter == PolicyTripleVariable.Bandwidth){
				if(operator == PolicyTripleOperator.GreaterThan && netValue > qosValue) return true;
				if(operator == PolicyTripleOperator.GreaterThanOrEquals && netValue >= qosValue) return true;
				if(operator == PolicyTripleOperator.Equals && netValue >= qosValue) return true;
				return false;
			}
			if(operator == PolicyTripleOperator.LessThan && netValue < qosValue) return true;
			if(operator == PolicyTripleOperator.LessThanOrEquals && netValue <= qosValue) return true;
			if(operator == PolicyTripleOperator.Equals && netValue <= qosValue) return true;
			return false;
		}else{
			return true;
		}
	}
}
