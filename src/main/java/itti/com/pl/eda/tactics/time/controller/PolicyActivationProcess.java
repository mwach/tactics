package itti.com.pl.eda.tactics.time.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import itti.com.pl.eda.tactics.policy.DateUnit;

/**
 * thread that is responsible for activating/deactivating policies
 * based on their time conditions
 * @author marcin
 *
 */
public class PolicyActivationProcess extends Thread{

	private static Logger logger = Logger.getLogger(PolicyActivationProcess.class);

	//keeps information about policies time events
	private List<TimeEvent> items = new ArrayList<TimeEvent>();
	private boolean running = false;

	private IPolicyActivationListener listener = null;


	@Override
	public void run() {

		logger.info("starting policyActivationProcess");

		running = true;

		while(running){

			Calendar cal = Calendar.getInstance();
			DateUnit currentDate = new DateUnit(
					cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), 
					cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));

			if(items != null && !items.isEmpty()){

				List<String> errors = new ArrayList<String>();

				for (TimeEvent item : items) {

					boolean matchData = item.isMatchData(currentDate);

					if(matchData && !item.isActive()){

						item.setActive(true);

						if(listener != null){
							boolean result = listener.activatePolicy(item.getPolicyId(), errors);
							if(result){
								logger.info("Policy with Id: " + item.getPolicyId() + " was successfully activated");
							}else{
								logger.warn("Activation failed for policy with Id: " + item.getPolicyId());
								logger.warn("Errors: " + errors.toString());
								errors.clear();
							}
						}

					}else if(!matchData && item.isActive()){

						item.setActive(false);

						if(listener != null){
							boolean result = listener.deactivatePolicy(item.getPolicyId(), errors);
							if(result){
								logger.info("Policy with Id: " + item.getPolicyId() + " was successfully deactivated");
							}else{
								logger.warn("Deactivation failed for policy with Id: " + item.getPolicyId());
								logger.warn("Errors: " + errors.toString());
								errors.clear();
							}
						}
					}
				}
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * add time event (which representing policy) to list of observed time events
	 * @param event time event object
	 */
	public void addTimeEvent(TimeEvent event){
		items.add(event);
	}

	/**
	 * removes time event (which representing policy) from list of observed time events
	 * @param event time event object
	 */
	public boolean removeTimeEvent(TimeEvent event){

		if(items != null && event != null){

			synchronized (items) {
				return items.remove(event);
			}
		}
		return false;
	}

	/**
	 * removes time event from list of observed time events
	 * @param policyId id of the policy which is related with time event
	 * @return true if process ends with success, false otherwise
	 */
	public boolean removeTimeEvent(long policyId){

		boolean removed = false;

		if(items != null && policyId > 0){

			synchronized (items) {
				for(int i=0 ; i<items.size() ; i++){
					if(items.get(i).getPolicyId() == policyId){
						removed = (items.remove(i) != null);
					}
				}
			}
		}
		return removed;
	}


	/**
	 * removes time event from list of observed time events
	 * @param userId id of the user, whose time evens should be removed from observed list
	 * @return true if process ends with success, false otherwise
	 */
	public boolean removeTimeEvents(long userId){

		boolean removed = false;

		if(items != null && userId > 0){

			synchronized (items) {
				for(int i=0 ; i<items.size() ; i++){
					if(items.get(i).getUserId() == userId){
						removed = (items.remove(i) != null);
					}
				}
			}
		}
		return removed;
	}


	/**
	 * sets reference to the policy activation process
	 * (which executes all operations required to change policy state)
	 * @param policyActivationListener 
	 */
	public void setEventListener(IPolicyActivationListener policyActivationListener) {
		listener = policyActivationListener;
	}
}
