package itti.com.pl.eda.tactics.ontology.controller;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.netqos.apm.policy.IntermediateCommonServicePolicy;
import itti.com.pl.eda.tactics.contextManager.ContextManagerPublisher;
import itti.com.pl.eda.tactics.contextManager.ContextManagerPublisher.CmEvents;
import itti.com.pl.eda.tactics.data.model.Ontology;
import itti.com.pl.eda.tactics.data.model.PolicyData;
import itti.com.pl.eda.tactics.data.model.PolicyHighLevel;
import itti.com.pl.eda.tactics.data.model.PolicyIntermediateLevel;
import itti.com.pl.eda.tactics.data.model.User;
import itti.com.pl.eda.tactics.data.translator.PolicyAPMTranslator;
import itti.com.pl.eda.tactics.data.translator.PolicyHLTranslator;
import itti.com.pl.eda.tactics.exception.AbstractPoldException;
import itti.com.pl.eda.tactics.exception.PoldControllerException;
import itti.com.pl.eda.tactics.exception.PoldControllerException.PoldControllerExceptionType;
import itti.com.pl.eda.tactics.exception.PoldHibernateException;
import itti.com.pl.eda.tactics.hibernate.controller.HibernateWrapper;
import itti.com.pl.eda.tactics.network.message.Argument;
import itti.com.pl.eda.tactics.network.message.ArgumentName;
import itti.com.pl.eda.tactics.network.message.Message;
import itti.com.pl.eda.tactics.network.message.MessageTypes;
import itti.com.pl.eda.tactics.network.message.OperatorCommands;
import itti.com.pl.eda.tactics.network.message.OperatorParameter;
import itti.com.pl.eda.tactics.network.message.TestCommand;
import itti.com.pl.eda.tactics.ontology.model.UserDataCache;
import itti.com.pl.eda.tactics.ontology.swrl.SwrlPolicyType;
import itti.com.pl.eda.tactics.ontology.swrl.SwrlPolicyTypesController;
import itti.com.pl.eda.tactics.policy.HLPolicy;
import itti.com.pl.eda.tactics.policy.PolicyTripleOperator;
import itti.com.pl.eda.tactics.policy.PolicyTripleVariable;
import itti.com.pl.eda.tactics.policy.impl.HLPolicyImpl;
import itti.com.pl.eda.tactics.policy.impl.PolicyElement;
import itti.com.pl.eda.tactics.policy.impl.PolicyTripleImpl;
import itti.com.pl.eda.tactics.time.controller.IPolicyActivationListener;
import itti.com.pl.eda.tactics.utils.PoldConstants;
import itti.com.pl.eda.tactics.utils.PoldResponses;
import itti.com.pl.eda.tactics.utils.PoldSettings;
import itti.com.pl.eda.tactics.utils.RefinementMethods;



/**
 * main class which contains ontology logic
 * @author marcin
 *
 */
public class PoldController implements ILocalPoldInterface, IPolicyActivationListener{

	private static Logger logger = Logger.getLogger(PoldController.class);

	private OntologyManager ontologyManager = null;
	private PolicyManager policyManager = null;
	private OntologyAssemblyLayer ontologyAssemblyLayer = null;
	public static HibernateWrapper hibernateWrapper = null;
	private Map<String, String> dbAttrs = null;
	private long policyOntologyId = -1;
	@SuppressWarnings("unused")
	private Ontology finalOntology = null;

	private PoldResponses poldStatus = null;

//	private PolicyActivationProcess policyActivationProcess = new PolicyActivationProcess();

	private List<String> listeners = new ArrayList<String>();

	ContextManagerPublisher cmPublisher = null;

	public boolean init(List<String> errors) {

		logger.info("Pold Controller init");

		boolean result = false;

		if(hibernateWrapper == null){

			try{

				logger.info("Hibernate wrapper initialization");

				hibernateWrapper = new HibernateWrapper();
				result = hibernateWrapper.init(dbAttrs, errors);

				logger.info("Initializing related policy managers");
				ontologyManager = new OntologyManager();
				policyManager = new PolicyManager();
				ontologyAssemblyLayer = new OntologyAssemblyLayer();

				logger.info("Refinement calculator initialization");
				List<Object[]> classPolicies = hibernateWrapper.getQoSClasses(errors);
				policyManager.initWeightsCalculator(classPolicies);

				logger.info("Loading default ontologies");

				loadDefaultOntologies(errors);

				logger.info("Initialization ends with success");

				result = true;
				poldStatus = PoldResponses.SystemInitOK;

			}catch (Exception e) {

				logger.warn("Errors during pold controller initialization");
				logger.warn(e.toString());
			}
		}
		return result;
	}


	public boolean dispose(){
		return true;
	}


	public void setDBParameters(String url, String user, String password){

		logger.info("Changing default hibernate settings");
		logger.info("user: " + user + ", url: " + url + ", pass: " + password);

		if(dbAttrs == null){
			dbAttrs = new Hashtable<String, String>();
		}else{
			dbAttrs.clear();
		}

		if(user != null){
			dbAttrs.put("connection.username", user);
			dbAttrs.put("hibernate.connection.username", user);
		}
		if(password != null){
			dbAttrs.put("connection.password", password);
			dbAttrs.put("hibernate.connection.password", password);
		}
		if(url != null){
			dbAttrs.put("connection.url", url);
			dbAttrs.put("hibernate.connection.url", url);
		}
	}


	private void loadDefaultOntologies(List<String> errors) throws PoldHibernateException{

		logger.info("Loading default ontologies");

		List<Ontology> defaultOntologies = hibernateWrapper.getDefaultOntologies(errors);

		for (Ontology ontology : defaultOntologies) {

			OntologyType type = ontology.getOntologyType();

			if(type == OntologyType.PolicyOntology
					|| type == OntologyType.FinalOntology){
				policyOntologyId = ontology.getId();
			}
			if(type == OntologyType.FinalOntology){
				finalOntology = ontology;
			}

			logger.info("Loading ontology type: " + type.name());

			try {
				policyManager.setOntology(type, ontologyAssemblyLayer.getOntologyModel(ontology));
				ontologyManager.setOntology(type, ontologyAssemblyLayer.getOntologyModel(ontology));

			} catch (AbstractPoldException e) {
				logger.warn("Errors during loading ontology type: " + type.name());
				logger.warn(e.toString());
				errors.add(e.toString());
			}
		}
	}


	public void runPolicyTypeUpdateProcess(){

		logger.info("runPolicyTypeUpdateProcess");

		List<String> errors = new ArrayList<String>();

		logger.info("Initializing SwrlPolicyTypesController");
		SwrlPolicyTypesController policyTypeController = new SwrlPolicyTypesController();
		boolean loadedRules = policyTypeController.loadPolicyTypesConfiguration(PoldSettings.PolicyTypesDefinitionsFile);

		if(loadedRules){

			logger.info("Swrl rules loaded. Updating policies");

			logger.info("Loading policies from DB");
			List<PolicyHighLevel> hpPolicies = hibernateWrapper.getHLPolicies(-1, errors);

			logger.info("Updating loaded ontologies");
			policyManager.updatePoliciesWithOntlology(hpPolicies);

			List<SwrlPolicyType> policyTypes = policyTypeController.getSwrlPolicyTypes();
			policyManager.prepareUpdatePoliciesTypesProcess(policyTypes);

			logger.info("Updating policies in database");

			for (PolicyHighLevel policyHighLevel : hpPolicies) {
				String type = policyManager.getPolicyTypeFromModel(policyHighLevel.getName());
				policyHighLevel.setType(type);
				hibernateWrapper.replaceHLPolicy(policyHighLevel.getId(), policyHighLevel, errors);
			}

		}else{
			logger.info("There are no swrl rules. Nothing will be updated");
		}
	}


	public boolean isActive(){
		return getState() == PoldResponses.SystemInitOK;
	}


	public boolean deletePolicy(long policyId, List<String> errors) {

		logger.info("delete policy with id: " + policyId);

		List<PolicyIntermediateLevel> pilList = hibernateWrapper.getILPoliciesByParentId(policyId, -1, errors);

		int deletedCnt = hibernateWrapper.deletePolicy(policyId, -1, errors);
		logger.info("Deleted policies count: " + deletedCnt);

		if(deletedCnt > 0){

//			logger.info("Removing policy from update timer thred");
//			policyActivationProcess.removeTimeEvent(policyId);

			logger.info("Notyfing listeners about delete operation");
			if(pilList != null){
				for (PolicyIntermediateLevel policyIntermediateLevel : pilList) {
					notifyListeners(CmEvents.PolicyRemoved, policyIntermediateLevel.getId());
				}
			}
		}

		return errors.isEmpty();
	}



	public boolean deleteUserPolicies(long userId, List<String> errors) {

		logger.info("delete all user policies. User id: " + userId);
		List<PolicyIntermediateLevel> pilList = hibernateWrapper.getILPoliciesByParentId(-1, userId, errors);

		int deletedCnt = hibernateWrapper.deletePolicy(-1, userId, errors);
		logger.info("Deleted policies count: " + deletedCnt);

		if(deletedCnt > 0){

//			logger.info("Removing policy from update timer thread");
//			policyActivationProcess.removeTimeEvents(userId);

			logger.info("Notyfing listeners about delete operation");
			if(pilList != null){
				for (PolicyIntermediateLevel policyIntermediateLevel : pilList) {
					notifyListeners(CmEvents.PolicyRemoved, policyIntermediateLevel.getId());
				}
			}
		}

		return errors.isEmpty();
	}


	public PolicyIntermediateLevel getPolicyIL(long policyId, List<String> errors) {

		PolicyIntermediateLevel pil = hibernateWrapper.getILPolicy(policyId, errors);
		return pil;
	}


	public List<PolicyIntermediateLevel> getActivePoliciesIL(String ipAddress, String appId, String policyType, List<String> errors) {

		return getPoliciesIL(ipAddress, appId, policyType, true, errors);
	}


	public List<PolicyIntermediateLevel> getPoliciesIL(String ipAddress, String policyType, List<String> errors) {

		return getPoliciesIL(ipAddress, null, policyType, false, errors);
	}


	public List<PolicyIntermediateLevel> getPoliciesIL(String ipAddress,
			String appType, String policyType, List<String> errors) {

		return getPoliciesIL(ipAddress, appType, policyType, false, errors);
	}

	private List<PolicyIntermediateLevel> getPoliciesIL(String ipAddress,
			String appType, String policyType, boolean activeOnly, List<String> errors) {

		List<PolicyIntermediateLevel> list = hibernateWrapper.getILPolicies(ipAddress, appType, policyType, activeOnly, errors);

		if(errors.isEmpty()){
			return list;
		}else{
			return null;
		}
	}


	public List<PolicyIntermediateLevel> getPoliciesILByParentId(long parentPolicyId, List<String> errors){

		List<PolicyIntermediateLevel> pilList = hibernateWrapper.getILPoliciesByParentId(parentPolicyId, -1, errors);

		if(errors.isEmpty()){
			return pilList;
		}else{
			return null;
		}
	}


	public List<PolicyHighLevel> getPoliciesHL(long userId, List<String> errors) {

		List<PolicyHighLevel> policies = hibernateWrapper.getHLPolicies(userId, errors);

		if(errors.isEmpty()){
			return policies;
		}else{
			return null;
		}
	}


	public PolicyHighLevel getPolicyHL(long policyId, List<String> errors) {

		PolicyHighLevel policy = hibernateWrapper.getHLPolicy(policyId, null, errors);

		if(errors.isEmpty()){
			return policy;
		}else{
			return null;
		}
	}


	public PolicyHighLevel getPolicyHL(String policyName, List<String> errors) {

		PolicyHighLevel policy = hibernateWrapper.getHLPolicy(-1, policyName, errors);

		if(errors.isEmpty()){
			return policy;
		}else{
			return null;
		}
	}


	public boolean replacePolicy(long policyId,
			PolicyHighLevel newPolicy, List<String> errors) {

		logger.info("Replacing policy with ID: " + policyId + " with new data");

		logger.info("Looking for oryginal police with id: " + policyId);
		PolicyHighLevel phl = hibernateWrapper.getHLPolicy(policyId, null, errors);

		if(phl != null){
			long userId = phl.getAuthor().getId();

			boolean prepared = preparePolicyToStoreInRepository(newPolicy, userId, false, errors);

			if(prepared){

				List<PolicyIntermediateLevel> pilList = policyManager.refinePolicy(newPolicy, errors);
				newPolicy.setChildren(pilList);

				boolean replaced = hibernateWrapper.replaceHLPolicy(policyId, newPolicy, errors);

				if(replaced){

					logger.info("Replace process ends with success");

//					logger.info("Updating policy timer thread with new data");
//					policyActivationProcess.removeTimeEvent(policyId);

//					TimeEvent timeEvent = null;
//					try {
//						timeEvent = TimeEventTranslator.getTimeEvent(newPolicy);
//
//					} catch (TimeConditionException e) {
//						logger.warn("Update timer process fails");
//						logger.warn(e.toString());
//						errors.add(e.toString());
//					}

//					if(timeEvent != null){
//						policyActivationProcess.addTimeEvent(timeEvent);
//					}

					logger.info("Notyfing listeners about update operation");
					List<PolicyIntermediateLevel> pillList = hibernateWrapper.getILPoliciesByParentId(policyId, -1, errors);
					if(pillList != null){
						for (PolicyIntermediateLevel policyIntermediateLevel : pillList) {
							notifyListeners(CmEvents.PolicyUpdated, policyIntermediateLevel.getId());
						}
					}

				}else{
					logger.info("Nothing was replaced");
				}

				return replaced;

			}else{
				logger.warn("Policy will not be replaced becouse of preparation process fails");
				return false;
			}
		}else{
			logger.warn("Oryginal policy with id: " + policyId + " doesn't exist in pold repository");
			errors.add("Oryginal policy with id: " + policyId + " doesn't exist in pold repository");
			return false;
		}
	}


	public boolean setPolicyILActive(long policyILId, boolean active, List<String> errors) {

		hibernateWrapper.changeILPolicyState(policyILId, true, errors);

		if(errors.isEmpty()){
			return true;
		}else{
			return false;
		}
	}


	public long storePolicy(long userId, PolicyHighLevel policyHL, List<String> errors) {

		boolean prepared = preparePolicyToStoreInRepository(policyHL, userId, true, errors);
		long policyId = -1;

		if(prepared && errors.isEmpty()){

			logger.info("Policy refinement process started...");
			List<PolicyIntermediateLevel> pilList = policyManager.refinePolicy(policyHL, errors);
			policyHL.setChildren(pilList);
			//TODO: temporary
//			try {
//				ontologyAssemblyLayer.saveOntology(ontologyManager.modelFinal, finalOntology);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}

			logger.info("Storing policy in pold database");
			policyId = storePolicies(policyHL.getAuthor().getId(), policyHL, errors);

			if(policyId != -1){

				logger.info("Notyfing listeners about instalation operation");
				try{
					if(policyHL.getChildren() != null){
						for (PolicyIntermediateLevel policyIntermediateLevel : policyHL.getChildren()) {
							notifyListeners(CmEvents.PolicyAdded, policyIntermediateLevel.getId());
						}
					}
				}catch (Exception e) {
					logger.error("Listeners notification failed");
					logger.error(e.toString());
				}
			}else{
				logger.warn("Policy will not be stored in pold repository");
			}
		}else{
			logger.warn("Policy will not be stored in pold repository");
		}

		return policyId;
	}


	private boolean preparePolicyToStoreInRepository(PolicyHighLevel phl, long userId, boolean newPolicy, List<String> errors){

		logger.info("Preparing new policy to store in pold repository");

		logger.info("Loading ontology related to the policy");
		try{
			phl.setOntology(ontologyAssemblyLayer.getOntology(OntologyType.FinalOntology));
		}catch (Exception e) {
			logger.warn("Errors during adding ontology to the policy");
			logger.warn(e.toString());
			errors.add(e.toString());
		}

		if(!errors.isEmpty()){
			return false;
		}

		logger.info("Loading policy owner");
		User user = hibernateWrapper.loadUser(userId, errors);

		if(user == null){
			logger.warn("Policy owner not found. Policy will not be installed");
			errors.add("Policy owner not found. Policy will not be installed");
			return false;
		}
		phl.setAuthor(user);

		//looking for other polices of selected author
		if(newPolicy){
			List<String> otherPolicies = hibernateWrapper.getHLPolicyNames(userId, errors);
			if(otherPolicies != null && !otherPolicies.isEmpty() && otherPolicies.contains(phl.getName())){
				errors.add("Policy with the same name exists in POLD database.");
			}
		}

		//TODO: must be - merging DB data and ontology data during retrieving data from DB
//		policyManager.validatePolicy(phl, errors);

//policyManager.addPolicyToOWLModel(phl);
		return errors.isEmpty();
	}


	public long storeAPMPolicy(IntermediateCommonServicePolicy apmPolicy,
			List<String> errorList) {

		logger.info("Translating APM policy into pold acceptable format");

		logger.info("Loading APM policy author from database. Actor name: " + apmPolicy.getActor());
		String actor = apmPolicy.getActor();
		User user = hibernateWrapper.loadUser(actor, errorList);

		if(user != null){
			logger.debug("APM policy author found in pold repository. Author id: " + user.getId());
		}else{
			logger.warn("APM user does not exist in the pold repository: " + actor + ". Policy will not be stored in pold repository");
			errorList.add("APM user does not exist in the pold repository: " + actor);
			return -1;
		}

		logger.debug("Adding APM default ontology to the APM policy");
		String apmOntology = "APM Ontology";
		Ontology ontology = hibernateWrapper.getOntology(apmOntology, errorList);

		PolicyIntermediateLevel pil = 
			PolicyAPMTranslator.getPolicyIntermediateLevel(apmPolicy, user, ontology);

		if(ontology != null){

			long freeAPMPolicyId = hibernateWrapper.getMaxPolicyIdForOntology(ontology.getId(), errorList) + 1;
			pil.getParent().setName("APM_Policy_" + freeAPMPolicyId);

		}else{
			errorList.add("APM ontology does not exist in the pold repository: " + apmOntology);
			logger.warn("APM ontology does not exist in the pold repository: " + apmOntology + ". Policy will not be stored in pold repository");

			return -1;
		}

		return storePolicies(user.getId(), pil.getParent(), errorList);
	}


	private long storePolicies(long userId, PolicyHighLevel policyHL, List<String> errorList){

		logger.info("storePolicies. UserId: " + userId);

		logger.info("Matching policy with other user policies");
		//TODO: matching disabled
//		List<PolicyHighLevel> userPolicies = hibernateWrapper.getHLPolicies(userId, errorList);
//		policyManager.matchPolicies(policyHL, userPolicies, errorList);

		if(!errorList.isEmpty()){
			logger.warn("Errors occurs. Policy will not be stored in pold database");
			return -1;
		}

		long newPolicyId = hibernateWrapper.storeHLPolicy(policyHL, errorList);

		return newPolicyId;
	}

	private Map<Long, UserDataCache> loadedUsers = new HashMap<Long, UserDataCache>();

	public UserDataCache getOntologyCache(long userId, List<String> errors){

		logger.info("Loading ontology cache for userId: " + userId);

		if(loadedUsers.containsKey(userId)){
			return loadedUsers.get(userId);
		}

		UserDataCache cache = null;

		try {

			if(ontologyManager == null){
					throw new PoldControllerException(PoldControllerExceptionType.OntologyManagerIsNull);
			}

			fillOntologyWithDbData(userId, errors);

			logger.info("Creating cache object with user data");
			cache = ontologyManager.getUserPolicyElements();

		} catch (PoldControllerException e) {
			logger.warn("Errors during loading user cache from database");
			logger.warn(e.toString());
			cache = null;
		}

		loadedUsers.put(userId, cache);

		return cache;
	}


	public List<String> getHLPolicyNames(long userId, List<String> errors) {

		List<PolicyHighLevel> policies = hibernateWrapper.getHLPolicies(userId, errors);
		if(!errors.isEmpty()){
			return null;
		}

		List<String> retList = new ArrayList<String>();
		for (PolicyHighLevel policy : policies) {
			retList.add(policy.getName());
		}
		return retList;
	}


	public long getUserId(String userName, List<String> errors) {

		User user = hibernateWrapper.loadUser(userName, errors);
		long id = user != null ? user.getId() : -1;

		if(id > 0){
			logger.info("Merging used data with ontologies");
			fillOntologyWithDbData(id, errors);
		}

		//TODO: temporary
//		try {
//			ontologyAssemblyLayer.saveOntology(ontologyManager.modelFinal, finalOntology);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}


		if(user != null){
			return user.getId();
		}else{
			return -1;
		}
	}


	private void fillOntologyWithDbData(long id, List<String> errors) {

		List<PolicyData> policyData = hibernateWrapper.getUserRelatedOntologyData(id, errors);
		ontologyManager.fillOntologyWithDbData(policyData);
	}


	public PoldResponses getState() {
		return poldStatus;
	}

	public List<PolicyIntermediateLevel> getPoliciesILByCriteria(PolicyTripleImpl criteria, List<String> errors) {

		List<PolicyIntermediateLevel> retList = hibernateWrapper.getILPoliciesByCriteria(criteria, errors);

		if(errors.isEmpty()){
			return retList;
		}else{
			return null;
		}
	}

	public Object processOperatorRequest(OperatorCommands operatorCommand,
			Map<OperatorParameter, String> argumentsMap, List<String> errors, Object object) {

		switch (operatorCommand) {
		case RunSqrlEngine:
			runPolicyTypeUpdateProcess();
			break;
		case SetRefinementEngine:
			return setRefinemenEngine(argumentsMap, errors);

		default:
			if(hibernateWrapper != null){
				return hibernateWrapper.processOperatorRequest(operatorCommand, argumentsMap, errors, object);
			}
			break;
		}
		return -1;
	}


	private boolean setRefinemenEngine(Map<OperatorParameter, String> argumentsMap, List<String> errors){

		String parameter = argumentsMap.get(OperatorParameter.Name);
		if(parameter == null){
			errors.add("Engine type not specified");
		}else{
			RefinementMethods method = RefinementMethods.getValue(parameter);
			if(method == null){
				errors.add("Invalid engine type: " + parameter);
			}else{
				PoldSettings.RefinementMethod = method;
				return true;
			}
		}
		return false;
	}



	public Object processTestRequest(TestCommand testCommand, long value,
			List<String> errors) {

		switch (testCommand) {

			case ReadOntologySize:
				return ontologyManager.getSizeOfTheOntology();
			case ReadRepositorySize:
				return hibernateWrapper.getNumberOfILPolicies(errors);
			case AddClassesToTheOntology:				return policyManager.addClassesToTheOntology(value);
			case PerformSelectOperation:
				return performSelectOperations(value, errors);
			case PerformInsertOperation:
				return performInsertOperations(value, false, errors);
			case PerformFullInsertOperation:
				return performInsertOperations(value, true, errors);
			case RemoveDummyClasses:
				return policyManager.removeAllDummyClasses();
			case RemoveDummyInstances:
				return policyManager.removeAllDummyInstances();
			default:
				break;
			}
		return null;
	}


	public boolean activatePolicy(long policyId, List<String> errors) {

		logger.info("Activating policy. PolicyId: " + policyId);

		boolean result = false;

		if(hibernateWrapper != null){

			logger.debug("Loading policy from database");

			PolicyHighLevel phl = hibernateWrapper.getHLPolicy(policyId, null, errors);

			if(phl != null && phl.getChildren() != null){

				result = true;

				//iterating policies to activate
				for (PolicyIntermediateLevel pil : phl.getChildren()) {
					pil.setActive(true);

					//updating database
					result &= hibernateWrapper.changeILPolicyState(pil.getId(), true, errors);

					//notify CM
					notifyListeners(CmEvents.PolicyUpdated, pil.getId());
				}

			}else if(phl == null){
				logger.warn("Policy not found in pold database");
			}else{
				logger.warn("Policy don't have any IL policies related");
			}

		}else{
			errors.add("Hibernate controller object is NULL");
		}
		return result;
	}

	public boolean deactivatePolicy(long policyId, List<String> errors) {

		logger.info("Deactivating policy. PolicyId: " + policyId);

		boolean result = false;

		if(hibernateWrapper != null){

			logger.debug("Loading policy from database");

			PolicyHighLevel phl = hibernateWrapper.getHLPolicy(policyId, null, errors);
			if(phl != null && phl.getChildren() != null){

				result = true;

				for (PolicyIntermediateLevel pil : phl.getChildren()) {
					pil.setActive(false);

					result &= hibernateWrapper.changeILPolicyState(pil.getId(), false, errors);

					notifyListeners(CmEvents.PolicyUpdated, pil.getId());
				}

			}else if(phl == null){
				logger.warn("Policy not found in pold database");
			}else{
				logger.warn("Policy don't have any IL policies related");
			}

		}else{
			errors.add("Hibernate controller object is NULL");
		}
		return result;
	}


	public boolean registerListener(String regAddress, List<String> errorList) {

		if(listeners.contains(regAddress)){
			errorList.add("Listener with given IP address: " + regAddress + " is already registened");
			return false;
		}
		return listeners.add(regAddress);
	}


	public boolean unregisterListener(String regAddress, List<String> errorList) {

		if(!listeners.contains(regAddress)){
			errorList.add("IP address: " + regAddress + " doesn't exist on listeners list");
			return false;
		}
		return listeners.remove(regAddress);
	}


	private boolean notifyListeners(CmEvents event, long policyId){

		if(event != null && policyId > 0){

			//first phase - notification for ContextManager
			logger.info("notification about event: " + event.name() + ", id: " + policyId);

//			cmPublisher = ContextManagerPublisher.getInstance();
//			if(cmPublisher != null){
//				try{
//					cmPublisher.processEvent(event, policyId);
//				}catch (Exception e) {
//				}
//			}else{
//				logger.warn("CM is null! Notification sending failed");
//			}

			//second phase - notification of all pold listeners
			if(listeners != null){

				PoldResponses response = null;
				switch(event){
					case PolicyAdded:
						response = PoldResponses.PolicyHLAddOK;
						break;
					case PolicyRemoved:
						response = PoldResponses.PolicyHLDeleteOK;
						break;
					case PolicyUpdated:
						response = PoldResponses.PolicyHLReplaceOK;
						break;
				}

				for (String address : listeners) {
					notifyListener(address, response, policyId);
				}
			}
		}
		return false;
	}

	private int portListeners = -1;
	public void setPortListeners(int portNr){
		portListeners = portNr;
		
	}

	private void notifyListener(String address, PoldResponses response, long policyId) {

		try {
			Message message = new Message(MessageTypes.Notification);
			message.addArgument(new Argument(ArgumentName.PolicyId, policyId));
			message.addArgument(new Argument(ArgumentName.Response, response.toString()));

			Socket socket = new Socket(address, portListeners);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(message);
			oos.close();
			socket.close();
		} catch (IOException e) {
			logger.error("Listener notification failed. Listener IP: " + address);
			logger.error(e.toString());
		}
	}

	/**
	 * for test cases
	 * @param value number of selects
	 * @return time of the single select
	 */
	private long performSelectOperations(long value, List<String> errors) {

		long startTime = new Date().getTime();

		for(int i=0 ; i<value ; i++){
			getPolicyHL((i+1), errors);
		}

		long endTime = new Date().getTime();

		return ((endTime - startTime) / value);
	}

	/**
	 * for test cases
	 * @param value number of selects
	 * @return time of the single select
	 */
	private long performInsertOperations(long value, boolean install, List<String> errors) {

		UserDataCache cache = getOntologyCache(1, errors);

		List<String> applications = cache.getInstancesList(PolicyElement.ConditionVariable, PolicyTripleVariable.Application.name());
		List<String> localizations = cache.getInstancesList(PolicyElement.ConditionVariable, PolicyTripleVariable.Localization.name());
		List<String> qosLevels = cache.getInstancesList(PolicyElement.ActionVariable, PolicyTripleVariable.Qos_level.name());
		String application = (applications != null && !applications.isEmpty()) ? applications.get(0): "dummy_app";
		String localization = (localizations != null && !localizations.isEmpty()) ? localizations.get(0): "dummy_loc";
		String qosLevel = (qosLevels != null && !qosLevels.isEmpty()) ? qosLevels.get(0): "dummy_qos";

		long maxPolicyId = hibernateWrapper.getMaxPolicyIdForOntology(policyOntologyId, errors);

		HLPolicy policy = new HLPolicyImpl();

		policy.addCondition(new PolicyTripleImpl(
				PolicyTripleVariable.Application, PolicyTripleOperator.Equals, application
		));
		policy.addCondition(new PolicyTripleImpl(
				PolicyTripleVariable.Localization, PolicyTripleOperator.Equals, localization
		));

		policy.addAction(new PolicyTripleImpl(
				PolicyTripleVariable.Qos_level, PolicyTripleOperator.Set, qosLevel
		));

		PolicyHighLevel phl = PolicyHLTranslator.getPolicyHighLevel(policy);

		long startTime = new Date().getTime();

		for(int i=0 ; i<value ; i++){

			phl.setName(PoldConstants.DummyInstanceName + (maxPolicyId++));
			errors.clear();
			if(install){
				storePolicy(1, phl, errors);
			}else{
				boolean prepared = preparePolicyToStoreInRepository(phl, 1, false, errors);
				if(prepared){
					List<PolicyIntermediateLevel> pilList = policyManager.refinePolicy(phl, errors);
					phl.setChildren(pilList);
				}
			}
		}

		long endTime = new Date().getTime();

		return ((endTime - startTime) / value);
	}
}
