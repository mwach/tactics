package itti.com.pl.eda.tactics.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.netqos.apm.policy.IntermediateCommonServicePolicy;
import itti.com.pl.eda.tactics.data.model.PolicyHighLevel;
import itti.com.pl.eda.tactics.data.model.PolicyIntermediateLevel;
import itti.com.pl.eda.tactics.data.translator.PolicyHLTranslator;
import itti.com.pl.eda.tactics.data.translator.PolicyILTranslator;
import itti.com.pl.eda.tactics.exception.ConfigException;
import itti.com.pl.eda.tactics.exception.PoldServerException;
import itti.com.pl.eda.tactics.network.message.ArgumentName;
import itti.com.pl.eda.tactics.network.message.Message;
import itti.com.pl.eda.tactics.network.message.OperatorCommands;
import itti.com.pl.eda.tactics.network.message.OperatorParameter;
import itti.com.pl.eda.tactics.network.message.TestCommand;
import itti.com.pl.eda.tactics.ontology.controller.ILocalPoldInterface;
import itti.com.pl.eda.tactics.ontology.controller.PoldController;
import itti.com.pl.eda.tactics.policy.HLPolicy;
import itti.com.pl.eda.tactics.policy.impl.PolicyTripleImpl;
import itti.com.pl.eda.tactics.utils.ConfigurationLoader;
import itti.com.pl.eda.tactics.utils.ConfigurationLoader.ConfigurationParameters;
import itti.com.pl.eda.tactics.utils.PoldResponses;
import itti.com.pl.eda.tactics.utils.PoldSettings;

//import com.jamonapi.Monitor;
//import com.jamonapi.MonitorFactory;


/**
 * pold server class
 * creates server, which handles all requests sent to the pold
 * @author marcin
 *
 */
public class PoldServer{

	static Logger logger = Logger.getLogger(PoldServer.class);

	private ILocalPoldInterface poldController = null;
	private static ConfigurationLoader configLoader = null;

	/**
	 * main class - initializes server
	 * @param args list of arguments
	 * @throws ConfigException
	 * @throws PoldServerException 
	 */
	public static void main(String[] args) throws ConfigException, PoldServerException{

//		Monitor monitor = MonitorFactory.start("PoldMain");

		logger.info("Server startup");
		PoldServer poldListenerServer = new PoldServer();

		poldListenerServer.prepareConfiguration();

		logger.info("Starting a listener server...");
		poldListenerServer.startListening();

//		//monitor.stop();
//		//System.out.println(monitor.toString());
	}


	/**
	 * prepares server to work
	 * @throws ConfigException
	 */
	public final void prepareConfiguration() throws ConfigException{

		logger.info("Configuration loader...");
		ConfigurationLoader configLoader = new ConfigurationLoader();
		configLoader.loadConfiguration(PoldSettings.ServerConfigurationFile);

		setConfiguation(configLoader);

	}

	private static void setConfiguation(ConfigurationLoader pConfigLoader) {
		configLoader = pConfigLoader;
	}

	public final void startListening() throws PoldServerException {

		logger.info("Loading controller...");
		poldController = new PoldController();
		List<String> errList = new ArrayList<String>();
		poldController.init(errList);

		if(!errList.isEmpty()){
			logger.error("ERRORS DURING POLD STARTUP!");
			logger.error(errList);
			throw new PoldServerException("Exceptions during server initialization", errList);
		}

		ClientListener requestsListener = null;
		OperatorListener operatorListener = null;

		try{

			logger.info("Starting a server...");
			int portClients = configLoader.getPropertyInt(ConfigurationParameters.port_clients);
			requestsListener = new ClientListener(portClients, poldController);

			int portOperators = configLoader.getPropertyInt(ConfigurationParameters.port_operators);
			operatorListener = new OperatorListener(portOperators, poldController);

			int portListeners = configLoader.getPropertyInt(ConfigurationParameters.port_listeners);
			((PoldController)poldController).setPortListeners(portListeners);

		}catch (ConfigException e) {
			errList.add(e.toString());
			logger.error(errList);
			System.exit(0);
		}

		logger.info("Starting listeners...");
		requestsListener.start();
		operatorListener.start();

		logger.info("POLD server initialization ends with SUCCESS...");
		logger.info("POLD server is ready for requests...");
	}


	/**
	 * thread which handles clients requests from pold
	 * @author marcin
	 *
	 */
	static class ClientListener extends Thread{

		private int port;
		private ServerSocket socket = null;
		private ILocalPoldInterface threadPoldController;

		public ClientListener(int port, ILocalPoldInterface poldController){
			this.port = port;
			threadPoldController = poldController;
		}

		@Override
		public void run() {

			try {
				socket = new ServerSocket(port);
				while(true){
					Socket clientSocket = socket.accept();
					ClientConnectionObjectHandler ch = 
						new ClientConnectionObjectHandler(clientSocket, threadPoldController);
					ch.start();
				}
			} catch (IOException e) {
				logger.error(e.toString());
			}
		}
	}


	/**
	 * thread which handles operators requests from pold
	 * @author marcin
	 *
	 */
	static class OperatorListener extends Thread{

		private int port;
		private ServerSocket socket = null;
		private ILocalPoldInterface threadPoldController;

		public OperatorListener(int port, ILocalPoldInterface poldController){
			this.port = port;
			threadPoldController = poldController;
		}

		@Override
		public void run() {

			try {
				socket = new ServerSocket(port);
				while(true){
					Socket clientSocket = socket.accept();
					ClientConnectionObjectHandler ch = 
						new ClientConnectionObjectHandler(clientSocket, threadPoldController);
					ch.start();
				}
			} catch (IOException e) {
				logger.error(e.toString());
			}
		}
	}


	/**
	 * class used to interact with single client or operator (instance per socket)
	 * @author marcin
	 *
	 */
	static class ClientConnectionObjectHandler extends Thread{

		private Socket connection = null;

		private ObjectInputStream ois = null;
		private ObjectOutputStream oos = null;

		private ILocalPoldInterface poldController;

		private Object poldObjectResponse = null;
		private List<String> errorList = new ArrayList<String>();


		/**
		 * default constructor
		 * @param socket tcp socket
		 * @param localPoldInterface reference to the local pold interface (pold controller)
		 */
		public ClientConnectionObjectHandler(Socket socket, ILocalPoldInterface localPoldInterface){
			connection = socket;
			poldController = localPoldInterface;
		}


		@Override
		public void run() {

			try{

				//Monitor clientMonitor = MonitorFactory.start("Client handler");

				ois = new ObjectInputStream(connection.getInputStream());
				oos = new ObjectOutputStream(connection.getOutputStream());

				//request type
				Object messageRequestObject = ois.readObject();
				Message clientMessage = Message.parseMessage(messageRequestObject);

				if(clientMessage != null){
					logger.info("Processing client request..." + clientMessage.getFormattedMessage());
				}

				//data object
				Object clientObject = ois.readObject();

				errorList.clear();

				switch (clientMessage.getMessageType()) {

					case Echo:

						boolean state = poldController.isActive();
						clientObject = state;
						break;

					case GetPolicyHL:

						getPolicyHL(clientMessage);
						break;

					case GetPolicyHLNames:

						getHLPolicyNamesList(clientMessage);
						break;

					case GetPolicyHLElements:

						getHLPolicyElementsList(clientMessage);
						break;

					case GetPolicyIL:

						getPoliciesILList(clientMessage);
						break;

					case GetPolicyILByType:

						getPoliciesILList(clientMessage);
						break;

					case GetPolicyILByCriteria:

						PolicyTripleImpl criteria = clientObject != null ? (PolicyTripleImpl)clientObject : null;
						getPoliciesILListByCriteria(clientMessage, criteria);
						break;

					case ActivateILPolicy:

						activatePolicy(clientMessage);
						break;

					case DeleteHLPolicy:
						
						deletePolicy(clientMessage);
						break;

					case StoreHLPolicy:

						HLPolicy phlBean = clientObject != null ? (HLPolicy)clientObject : null;
						storePolicy(clientMessage, phlBean);
						break;

					case ReplaceHLPolicy:

						phlBean = clientObject != null ? (HLPolicy)clientObject : null;
						replaceHLPolicy(clientMessage, phlBean);
						break;

					case GetUserID:

						getUserId(clientMessage);
						break;

					case StoreAPMPolicy:

						IntermediateCommonServicePolicy apmPolicy = clientObject != null ? (IntermediateCommonServicePolicy)clientObject : null;
						storeAPMPolicy(apmPolicy);
						break;

					case OperatorRequest:

						processOperatorRequest(clientMessage, clientObject);
						break;

					case RegisterListener:
						String regAddress = 
							this.connection.getInetAddress().getHostAddress();
						registerNewListener(regAddress);
						break;

					case UnregisterListener:
						String unregAddress = 
							this.connection.getInetAddress().getHostAddress();
						unregisterListener(unregAddress);
						break;

					case TestCase:
						processTestCase(clientMessage);
						break;

					default:
						logger.warn("Unrecognized client request");
						logger.warn(clientMessage);
						break;
				}
				sendResponse(poldObjectResponse, errorList);

				//read a conf from client
				ois.readObject();

//				clientMonitor.stop();
//				System.out.println(clientMonitor);

			}catch (Exception e) {
				e.printStackTrace();
				logger.error(e.toString());
			}finally{
				closeConnection();
			}
		}


		private void sendResponse(Object object, Collection<String> errList) throws IOException {

			if(object == null){
				oos.writeObject("");
			}else{
				oos.writeObject(object);
			}
			oos.writeObject(errList);
		}


		private void closeConnection() {
			try{
				if(ois != null){
					ois.close();
				}
				if(oos != null){
					oos.close();
				}
				if(connection != null){
					connection.close();
				}

				ois = null;
				oos = null;
				connection = null;

			}catch(Exception e){
				logger.error(e.toString());
			}
		}



		private void getPolicyHL(Message message) {

//			Monitor monitor = MonitorFactory.start("getPolicyHL");
			try{

				long userId = message.getArgumentValueLong(ArgumentName.UserId);
				long policyId = message.getArgumentValueLong(ArgumentName.PolicyId);
				String policyName = message.getArgumentValue(ArgumentName.PolicyName);

				List<PolicyHighLevel> list = null;

				if(userId != -1){
					list = poldController.getPoliciesHL(userId, errorList);
					Collection<HLPolicy> retList = PolicyHLTranslator.getPoliciesHLBeans(list);
					poldObjectResponse = retList;

				}else if(policyName != null){
					PolicyHighLevel phl = poldController.getPolicyHL(policyName, errorList);
					poldObjectResponse = PolicyHLTranslator.getPolicyHLBean(phl);

				}else if(policyId != -1){
					PolicyHighLevel phl = poldController.getPolicyHL(policyId, errorList);
					poldObjectResponse = PolicyHLTranslator.getPolicyHLBean(phl);
				}

			}catch(Exception e){
				errorList.add(e.toString());
				logger.error(errorList);
			}
//			//monitor.stop();
//			//System.out.println(monitor.toString());
		}


		private void getHLPolicyNamesList(Message message) {

			//Monitor monitor = MonitorFactory.start("getHLPolicyNamesList");
			try{
				long userId = message.getArgumentValueLong(ArgumentName.UserId);
				Collection<String> list = poldController.getHLPolicyNames(userId, errorList);

				poldObjectResponse = list;

			}catch(Exception e){
				errorList.add(e.toString());
				logger.error(errorList);
			}
			//monitor.stop();
			//System.out.println(monitor.toString());
		}



		private void getHLPolicyElementsList(Message message) {

			//Monitor monitor = MonitorFactory.start("getHLPolicyElementsList");
			try{
				long userId = message.getArgumentValueLong(ArgumentName.UserId);
				Object cacheObject = poldController.getOntologyCache(userId, errorList);

				poldObjectResponse = cacheObject;

			}catch(Exception e){
				errorList.add(e.toString());
				logger.error(errorList);
			}
			//monitor.stop();
			//System.out.println(monitor.toString());
		}



		private void getPoliciesILList(Message message) {

			//Monitor monitor = MonitorFactory.start("getPoliciesILList");
			try{

				long hlPolicyId = message.getArgumentValueLong(ArgumentName.PolicyHLId);

				long policyId = message.getArgumentValueLong(ArgumentName.PolicyId);

				String ipAddress = message.getArgumentValue(ArgumentName.IpAddr);
				String appType = message.getArgumentValue(ArgumentName.AppType);
	
				boolean state = message.getArgumentValueBoolean(ArgumentName.State);
				String policyType = message.getArgumentValue(ArgumentName.PolicyType);

				Collection<PolicyIntermediateLevel> list = null;

				if(hlPolicyId != -1){

					list = poldController.getPoliciesILByParentId(hlPolicyId, errorList);
					poldObjectResponse = PolicyILTranslator.getPoliciesILBeans(list);

				}else if(policyId != -1){
					PolicyIntermediateLevel pil = poldController.getPolicyIL(policyId, errorList);
					poldObjectResponse = PolicyILTranslator.getPolicyILBean(pil);

				}else{
					if(state){
						list = poldController.getActivePoliciesIL(ipAddress, appType, policyType, errorList);
					}else{
						list = poldController.getPoliciesIL(ipAddress, appType, policyType, errorList);
					}
					poldObjectResponse = PolicyILTranslator.getPoliciesILBeans(list);
				}

			}catch(Exception e){
				errorList.add(e.toString());
				logger.error(errorList);
			}
			//monitor.stop();
			//System.out.println(monitor.toString());
		}

		
		private void getPoliciesILListByCriteria(
				Message clientMessage, PolicyTripleImpl criteria) {

			//Monitor monitor = MonitorFactory.start("getPoliciesILListByCriteria");
			try{
				List<PolicyIntermediateLevel> policiesList = poldController.getPoliciesILByCriteria(criteria, errorList);
				poldObjectResponse = PolicyILTranslator.getPoliciesILBeans(policiesList);

			}catch(Exception e){
				errorList.add(e.toString());
				logger.error(errorList);
			}
			//monitor.stop();
			//System.out.println(monitor.toString());
		}


		private PoldResponses activatePolicy(Message message) {

			//Monitor monitor = MonitorFactory.start("activatePolicy");
			PoldResponses response = PoldResponses.PolicyILActivationOK;

			try{

				long policyId = message.getArgumentValueLong(ArgumentName.PolicyId);
				boolean state = message.getArgumentValueBoolean(ArgumentName.State);
	
				boolean result = poldController.setPolicyILActive(policyId, state, errorList);
				if(!result){
					response = PoldResponses.PolicyILActivationFailed;
				}

				poldObjectResponse = result;

			}catch(Exception e){
				errorList.add(e.toString());
				logger.error(errorList);
				response = PoldResponses.PolicyILActivationFailed;
			}
			//monitor.stop();
			//System.out.println(monitor.toString());
			return response;
		}

		
		private PoldResponses replaceHLPolicy(Message message, HLPolicy policy) {

			//Monitor monitor = MonitorFactory.start("replaceHLPolicy");

			PoldResponses response = PoldResponses.PolicyHLReplaceOK;

			try{

				long policyId = message.getArgumentValueLong(ArgumentName.PolicyId);
	
				PolicyHighLevel phl = PolicyHLTranslator.getPolicyHighLevel(policy);
	
				boolean result = poldController.replacePolicy(policyId, phl, errorList);

				poldObjectResponse = result;

			}catch(Exception e){
				errorList.add(e.toString());
				logger.error(errorList);
				response = PoldResponses.PolicyHLReplaceFailed;
			}

			//monitor.stop();
			//System.out.println(monitor.toString());
			return response;
		}


		private PoldResponses deletePolicy(Message message) {

			//Monitor monitor = MonitorFactory.start("deletePolicy");

			PoldResponses response = PoldResponses.PolicyHLDeleteOK;

			try{

				long policyId = message.getArgumentValueLong(ArgumentName.PolicyId);
				long userId = message.getArgumentValueLong(ArgumentName.UserId);
	
				boolean result = false;
				if(policyId != -1){
					result = poldController.deletePolicy(policyId, errorList);
				}else{
					result = poldController.deleteUserPolicies(userId, errorList);
				}

				poldObjectResponse = result;

			}catch(Exception e){
				errorList.add(e.toString());
				logger.error(errorList);
				response = PoldResponses.PolicyHLDeleteFailed;
			}
			//monitor.stop();
			//System.out.println(monitor.toString());
			return response;
		}


		private PoldResponses storePolicy(Message message, HLPolicy phl) {

			//Monitor monitor = MonitorFactory.start("storePolicy");

			PoldResponses response = PoldResponses.PolicyHLAddOK;

			try{
//				long date1 = new Date().getTime();
				long userId = message.getArgumentValueLong(ArgumentName.UserId);
	
				PolicyHighLevel policy = PolicyHLTranslator.getPolicyHighLevel(phl);
	
				long policyId = poldController.storePolicy(userId, policy, errorList);

				poldObjectResponse = policyId;
				if(policyId == -1){
					response = PoldResponses.PolicyHLAddFailed;
				}
//				System.out.println("Inserting: " + (new Date().getTime() - date1));
			}catch(Exception e){
				errorList.add(e.toString());
				logger.error(errorList);
				response = PoldResponses.PolicyHLAddFailed;
			}
			//monitor.stop();
			//System.out.println(monitor.toString());
			return response;
		}


		private void getUserId(Message message) {

			try{
				String userName = message.getArgumentValue(ArgumentName.UserName);

				long id = poldController.getUserId(userName, errorList);
		
				poldObjectResponse = id;
		
			}catch(Exception e){
				errorList.add(e.toString());
				logger.error(errorList);
			}
		}

		
		private PoldResponses storeAPMPolicy(
				IntermediateCommonServicePolicy apmPolicy) {

			PoldResponses response = PoldResponses.PolicyHLAddOK;

			try{
				long id = poldController.storeAPMPolicy(apmPolicy, errorList);

				poldObjectResponse = id;

			}catch(Exception e){
				errorList.add(e.toString());
				logger.error(errorList);
				response = PoldResponses.PolicyHLAddFailed;
			}
			return response;
		}


		private boolean processOperatorRequest(Message clientMessage, Object object) {

			String operatorCommandStr = clientMessage.getArgumentValue(ArgumentName.Operator);
			OperatorCommands operatorCommand = OperatorCommands.getOperatorCommand(operatorCommandStr);

			Map<OperatorParameter, String> argumentsMap = clientMessage.getOperatorArgumentsMap();

			poldObjectResponse = poldController.processOperatorRequest(operatorCommand, argumentsMap, errorList, object);

			return errorList.isEmpty();
		}


		private boolean processTestCase(Message clientMessage) {

			String testCommandStr = clientMessage.getArgumentValue(ArgumentName.Test);
			TestCommand testCommand = TestCommand.getTestCommand(testCommandStr);

			long value = -1;
			try{
				value = clientMessage.getArgumentValueLong(ArgumentName.TestParam);
			}catch (Exception e) {
				value = -1;
			}

			poldObjectResponse = poldController.processTestRequest(testCommand, value, errorList);

			return errorList.isEmpty();
		}

		private void registerNewListener(String regAddress) {
			poldObjectResponse = poldController.registerListener(regAddress, errorList);
		}

		private void unregisterListener(String regAddress) {
			poldObjectResponse = poldController.unregisterListener(regAddress, errorList);
		}
	}
}