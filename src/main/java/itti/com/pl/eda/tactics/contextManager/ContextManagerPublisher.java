package itti.com.pl.eda.tactics.contextManager;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import it.unina.comics.events.NewPolicyAddedToRepositoryEvent;
import it.unina.comics.events.PolicyDeletedFromRepositoryEvent;
import it.unina.comics.events.ResourceReservationPolicyUpdatedEvent;
import it.unina.comics.netqos.client.NetQoSClientPublisher;


/**
 * an interface to interact with ContextManager component
 * @author marcin
 *
 */
public class ContextManagerPublisher {


	private static final Logger logger = Logger.getLogger(ContextManagerPublisher.class);


	/**
	 * available events types
	 * @author marcin
	 *
	 */
	public enum CmEvents{
		PolicyAdded,
		PolicyRemoved,
		PolicyUpdated,
	}

	private static ContextManagerPublisher cmp = null;
	private static Object synchrObj = new Object();
	private NetQoSClientPublisher publisher = null;



	/**
	 * singletone pattern is used
	 * method returns an instance to ContextManager publisher object
	 * @return instance of ContextManagerPublisher object
	 */
	public static ContextManagerPublisher getInstance(){

		synchronized (synchrObj) {

			if(cmp == null){

				logger.info("CM instance will be initialized");

				cmp = new ContextManagerPublisher();
			}

			return cmp;
		}
	}


	private ContextManagerPublisher(){
		init();
	}


	private void init() {

		logger.info("CM initialization");

		ArrayList<String> eventsList = new ArrayList<String>();
		eventsList.add("newpolicyaddedtorepository");
		eventsList.add("policydeletedfromrepository");
		publisher = new NetQoSClientPublisher();

		logger.info("Starting a CM thread");

		publisher.start();

		logger.info("Sleep invoked");

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		logger.info("Publishing list of events to CM server");

		try{
			publisher.PublishList(eventsList);
			logger.info("CM connection successfully initialized");

		}catch (Exception e) {
			logger.error("Errors during publishing events list");
			logger.error(e.toString());
		}
	}


	private boolean addPolicyEvent(long policyId){

		logger.info("CM event - addPolicy. PolicyId = " + policyId);

		boolean result = false;
		try{
			NewPolicyAddedToRepositoryEvent rrpae = new NewPolicyAddedToRepositoryEvent(this);
			rrpae.setPolicyId(String.valueOf(policyId));

			logger.info("Sending notification to the CM server");

			publisher.sendNotification(rrpae);
			result = true;

			logger.info("Notification successfully sent");

		}catch (Exception e) {
			logger.error("Errors during sending notification");
			logger.error(e.toString());
		}
		return result;
	}


	private boolean updatePolicyEvent(long policyId){

		logger.info("CM event - updatePolicy. PolicyId = " + policyId);

		boolean result = false;
		try{
			ResourceReservationPolicyUpdatedEvent rrpae = new ResourceReservationPolicyUpdatedEvent(this);
			rrpae.setPolicyId(String.valueOf(policyId));

			logger.info("Sending notification to the CM server");

			publisher.sendNotification(rrpae);
			result = true;

			logger.info("Notification successfully sent");

		}catch (Exception e) {
			logger.error("Errors during sending notification");
			logger.error(e.toString());
		}
		return result;
	}


	private boolean removePolicy(long policyId){

		logger.info("CM event - removePolicy. PolicyId = " + policyId);

		boolean result = false;
		try{
			PolicyDeletedFromRepositoryEvent rrpae = new PolicyDeletedFromRepositoryEvent(this);
			rrpae.setPolicyId(String.valueOf(policyId));

			logger.info("Sending notification to the CM server");

			publisher.sendNotification(rrpae);
			result = true;

			logger.info("Notification successfully sent");

		}catch (Exception e) {
			logger.error("Errors during sending notification");
			logger.error(e.toString());
		}
		return result;
	}


	/**
	 * sends an information to the remote ContextManager server about event
	 * @param event event type
	 * @param policyId id of the affected policy
	 * @return true, if process ends with success, false otherwise
	 */
	public boolean processEvent(CmEvents event, long policyId) {

//		if(publisher == null || !publisher.isAlive()){
//			init();
//		}

		if(event != null && policyId > 0){
			if(event == CmEvents.PolicyAdded){

				return addPolicyEvent(policyId);

			}else if(event == CmEvents.PolicyRemoved){

				return removePolicy(policyId);

			}else if(event == CmEvents.PolicyUpdated){

				return updatePolicyEvent(policyId);

			}else{
				return false;
			}
		}else{
			return false;
		}
	}
}
