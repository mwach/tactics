package itti.com.pl.eda.tactics.operator;

import java.io.Serializable;

import itti.com.pl.eda.tactics.policy.Condition;

public class Network implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long id = -1;
	private String name = null;

	private int delay = -1;
	private int jitter = -1;
	private int bandwidth = -1;
	private int loss = -1;

	private Condition condDelay = null;
	private Condition condJitter = null;
	private Condition condBandwidth = null;
	private Condition condLoss = null;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
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
	public void setDelay(Condition sign, int delay) {
		this.condDelay = sign;
		this.delay = delay;
	}

	/**
	 * sets jiter value
	 * @param jitter
	 */
	public void setJitter(Condition sign, int jitter) {
		this.condJitter = sign;
		this.jitter = jitter;
	}

	/**
	 * sets bandwidth value
	 * @param bandwidth
	 */
	public void setBandwidth(Condition sign, int bandwidth) {
		this.condBandwidth = sign;
		this.bandwidth = bandwidth;
	}

	/**
	 * sets loss value
	 * @param loss
	 */
	public void setLoss(Condition sign, int loss) {
		this.condLoss = sign;
		this.loss = loss;
	}

	@Override
	public String toString() {
		return id + ": " + name;
	}
	public Condition getCondDelay() {
		return condDelay;
	}
	public Condition getCondJitter() {
		return condJitter;
	}
	public Condition getCondBandwidth() {
		return condBandwidth;
	}
	public Condition getCondLoss() {
		return condLoss;
	}
}
