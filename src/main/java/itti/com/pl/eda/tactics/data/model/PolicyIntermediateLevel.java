package itti.com.pl.eda.tactics.data.model;

import java.io.Serializable;

import itti.com.pl.eda.tactics.policy.PolicyTriple;
import itti.com.pl.eda.tactics.policy.TimeCondition;

/**
 * transporting class used to keep information about single intermediate-level policy
 * @author marcin
 *
 */
public class PolicyIntermediateLevel implements Serializable{

	private static final long serialVersionUID = 1L;

	private long id = -1;
	private String ipAddress = null;
	private String application = null;
	private User author = null;
	private int weight = -1;
	private boolean active = false;
	private PolicyHighLevel parent = null;
	private String type = null;
	private QoSLevel qosLevel = null;

	private PolicyTriple conditions = null;
	private PolicyTriple actions = null;
	private TimeCondition timeConditions = null;


	/**
	 * returns policy id
	 * @return policy id
	 */
	public long getId() {
		return id;
	}

	/**
	 * sets policy id
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}


	/**
	 * sets policy conditions
	 * @param conditions
	 */
	public void setConditions(PolicyTriple conditions){
		this.conditions = conditions;
	}

	/**
	 * returns policy conditions
	 * @return conditions
	 */
	public PolicyTriple getConditions(){
		return conditions;
	}

	/**
	 * sets policy time conditions
	 * @param timeConditions
	 */
	public void setTimeConditions(TimeCondition timeConditions){
		this.timeConditions = timeConditions;
	}

	/**
	 * returns policy time conditions
	 * @return timeConditions
	 */
	public TimeCondition getTimeConditions(){
		return timeConditions;
	}

	/**
	 * sets policy actions
	 * @param actions
	 */
	public void setActions(PolicyTriple actions){
		this.actions = actions;
	}

	/**
	 * returns policy actions
	 * @return actions
	 */
	public PolicyTriple getActions(){
		return actions;
	}

	/**
	 * sets qos level related to the policy
	 * @param qosLevel
	 */
	public void setQosLevel(QoSLevel qosLevel) {
		this.qosLevel = qosLevel;
	}

	/**
	 * returns qos level related to the policy
	 * @return qosLevel
	 */
	public QoSLevel getQosLevel() {
		return qosLevel;
	}


	/**
	 * sets policy 'weight' calculated during refinement process
	 * @param weight
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}

	/**
	 * returns policy 'weight' calculated during refinement process
	 * @return weight
	 */
	public int getWeight(){
		return weight;
	}

	/**
	 * sets true if policy should be active, false otherwise
	 * @param active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * returns true if policy is active, false otherwise
	 * @return active
	 */
	public boolean isActive(){
		return active;
	}


	/**
	 * sets 'parent' hl policy 
	 * @param parent
	 */
	public void setParent(PolicyHighLevel parent){
		this.parent = parent;
	}

	/**
	 * return 'parent' hl policy
	 * @return parent
	 */
	public PolicyHighLevel getParent(){
		return parent;
	}

	/**
	 * returns swrl-defined policy type
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * sets swrl-defined policy type
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * sets ip address related to the policy
	 * @param ipAddress
	 */
	public void setIpAddress(String ipAddress){
		this.ipAddress = ipAddress;
	}

	/**
	 * returns ip address related to the policy
	 * @return ipAddress
	 */
	public String getIpAddress(){
		return ipAddress;
	}

	/**
	 * returns application related to the policy
	 * @return application
	 */
	public String getApplication() {
		return application;
	}

	/**
	 * sets application related to the policy
	 * @param application
	 */
	public void setApplication(String application) {
		this.application = application;
	}

	/**
	 * returns policy author
	 * @return author
	 */
	public User getAuthor() {
		return author;
	}

	/**
	 * sets policy author
	 * @param author
	 */
	public void setAuthor(User author) {
		this.author = author;
	}

	@Override
	public String toString(){

		return "id: " + id + ", ip: " + ipAddress + ", application: " + application + 
			", QosLevel: " + qosLevel;
	}

}
