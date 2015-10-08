package itti.com.pl.eda.tactics.policy.impl;

import java.io.Serializable;

import eu.netqos.utils.policies.ConnectionId;
import itti.com.pl.eda.tactics.policy.ILMeasureInterface;
import itti.com.pl.eda.tactics.policy.ILPolicy;
import itti.com.pl.eda.tactics.policy.ILReservationInterface;
import itti.com.pl.eda.tactics.policy.IQoSLevel;
import itti.com.pl.eda.tactics.policy.LogicalOperator;
import itti.com.pl.eda.tactics.policy.PolicyTriple;
import itti.com.pl.eda.tactics.policy.PolicyTripleVariable;
import itti.com.pl.eda.tactics.policy.TimeCondition;

/**
 * policy intermediate object
 *
 */
public class ILPolicyImpl implements ILPolicy, ILReservationInterface, ILMeasureInterface, Serializable{

	private static final long serialVersionUID = 1L;
	private long id = -1;
	private int weight = -1;
	private boolean active;
	private String description = null;

	private IQoSLevel qosLevel = null;

	private PolicyTriple actions = new PolicyTripleImpl();
	private PolicyTriple conditions = new PolicyTripleImpl();
	private TimeCondition timeConditions = null;

	private ConnectionId connectionId = null;

	private String authorName = null;
	private long authorId = -1;

	public void setId(long i){
		id = i;
	}

	public long getId(){
		return id;
	}

	public int getBandwidth() {
		return qosLevel != null ? qosLevel.getBandwidth() : 0;
	}
	public int getDelay() {
		return qosLevel != null ? qosLevel.getDelay() : 0;
	}
	public int getJitter() {
		return qosLevel != null ? qosLevel.getJitter() : 0;
	}
	public int getLoss() {
		return qosLevel != null ? qosLevel.getLoss() : 0;
	}

	public IQoSLevel getQoSLevel(){
		return qosLevel;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String s){
		description = s;
	}

	public int getWeight() {
		return weight;
	}
	public void setWeight(int w){
		weight = w;
	}

	public String getIpAddress(){

		if(conditions != null){
			return conditions.getElementValue(PolicyTripleVariable.IpAddress);
		}else{
			return null;
		}
	}

	public String getNetwork(){

		if(conditions != null){
			return conditions.getElementValue(PolicyTripleVariable.Network);
		}else{
			return null;
		}
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean pActive) {
		active = pActive;
	}

	public void setActions(PolicyTriple pActions) {
		actions = pActions;
	}
	public PolicyTriple getActions(){
		return actions;
	}

	public void setConditions(PolicyTriple pConditions) {
		conditions = pConditions;
	}
	public PolicyTriple getConditions(){
		return conditions;
	}

	public void setQoSLevel(IQoSLevel level) {
		this.qosLevel = level;
	}


	public void setTimeConditions(TimeCondition tc){
		timeConditions = tc;
	}
	public TimeCondition getTimeConditions(){
		return timeConditions;
	}

	public void addTimeCondition(TimeCondition tc){
		if(timeConditions == null){
			timeConditions = new TimeCondition(LogicalOperator.And);
		}
		if(tc != null){
			timeConditions.addCondition(tc);
		}
	}


	public void setConnectionId(ConnectionId connectionId){
		this.connectionId = connectionId;
	}

	public ConnectionId getConnectionId(){
		return connectionId;
	}


	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id: " + id + "\n");
		sb.append("weight: " + weight + "\n");
		sb.append("active: " + active + "\n");
		sb.append("IP: " + getIpAddress() + "\n");
		sb.append("Network: " + getNetwork() + "\n");
		sb.append(qosLevel != null ? qosLevel.toString() : "");
		return sb.toString();
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public void setAuthorId(long authorId) {
		this.authorId = authorId;
	}

	public String getAuthorName() {
		return authorName;
	}
	public long getAuthorId() {
		return authorId;
	}
}
