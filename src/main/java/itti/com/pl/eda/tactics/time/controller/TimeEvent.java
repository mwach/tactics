package itti.com.pl.eda.tactics.time.controller;

import itti.com.pl.eda.tactics.policy.DateUnit;
import itti.com.pl.eda.tactics.policy.TimeCondition;

/**
 * keeps information about time conditions for single policy
 * @author marcin
 *
 */
public class TimeEvent {

	private long policyId = -1;
	private long userId = -1;
	private TimeCondition timeConditions = null;
	private boolean active = false;

	
	/**
	 * default constructor
	 * @param policyId id of the policy
	 * @param userId id of the user (policy author)
	 * @param timeConditions list of policy time conditions
	 */
	public TimeEvent(long policyId, long userId, TimeCondition timeConditions){
		this.policyId = policyId;
		this.userId = userId;
		this.timeConditions = timeConditions;
	}

	/**
	 * set state of the timeEvent
	 * @param state
	 */
	public void setActive(boolean state) {
		active = state;
	}

	/**
	 * returns status of the TimeEvent
	 * @return true, if object is in active state, false otherwise
	 */
	public boolean isActive(){
		return active;
	}


	/**
	 * @return policyId
	 */
	public long getPolicyId(){
		return policyId;
	}

	/**
	 * @return userId
	 */
	public long getUserId(){
		return userId;
	}

	/**
	 * checks, if policy time conditions match given data
	 * @param currentDate current date
	 * @return true, if timeEvent match specified data, false otherwise
	 */
	public boolean isMatchData(DateUnit currentDate){

		if(timeConditions == null){
			return false;
		}else{
			return timeConditions.isActive(currentDate);
		}
	}

	@Override
	public boolean equals(Object obj) {

		if(obj == null || !(obj instanceof TimeEvent)){

			return false;

		}else{

			TimeEvent te = (TimeEvent)obj;
			return te.getPolicyId() == getPolicyId();
		}
	}


	@Override
	public String toString() {
		return "pId: " + policyId + ", uId: " + userId + (timeConditions != null ? "\n" + timeConditions.toString() : "");
	}

}
