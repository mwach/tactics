package itti.com.pl.eda.tactics.data.model;

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


	public Application(){
	}


	public Application(String name, String type){
		this.name = name;
		this.type = type;
	}


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
}
