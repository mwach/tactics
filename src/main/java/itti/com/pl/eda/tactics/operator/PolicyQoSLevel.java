package itti.com.pl.eda.tactics.operator;

import itti.com.pl.eda.tactics.policy.IQoSLevel;

public class PolicyQoSLevel implements IQoSLevel{

	private static final long serialVersionUID = 1L;

	private long id = -1;

	private String name;

	private int bandwidth = -1;
	private int jitter = -1;
	private int delay = -1;
	private int loss = -1;

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

	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQosName() {
		return name + "_qos";
	}

	@SuppressWarnings("unused")
	private void setQosName(String name) {
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
	 * @param jiter
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
		StringBuffer sb = new StringBuffer();
		sb.append("name: " + name + "\n");
		return sb.toString();
	}
}
