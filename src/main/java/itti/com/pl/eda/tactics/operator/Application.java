package itti.com.pl.eda.tactics.operator;

import java.io.Serializable;

import itti.com.pl.eda.tactics.policy.Condition;

public class Application implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id = -1;
	private String name = null;
	private String type = null;

	private int delay = -1;
	private int jitter = -1;
	private int bandwidth = -1;
	private int loss = -1;

	private Condition condBandwidth = null;
	private Condition condDelay = null;
	private Condition condJitter = null;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getDelay() {
		return delay;
	}
	public void setDelay(int delay) {
		this.delay = delay;
	}
	public int getJitter() {
		return jitter;
	}
	public void setJitter(int jitter) {
		this.jitter = jitter;
	}
	public int getBandwidth() {
		return bandwidth;
	}
	public void setBandwidth(int bandwidth) {
		this.bandwidth = bandwidth;
	}
	public int getLoss() {
		return loss;
	}
	public void setLoss(int loss) {
		this.loss = loss;
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
}
