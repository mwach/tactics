package itti.com.pl.eda.tactics.ontology.controller;

import java.util.List;
import java.util.Map;

import eu.netqos.apm.policy.IntermediateCommonServicePolicy;
import itti.com.pl.eda.tactics.data.model.PolicyHighLevel;
import itti.com.pl.eda.tactics.data.model.PolicyIntermediateLevel;
import itti.com.pl.eda.tactics.network.message.OperatorCommands;
import itti.com.pl.eda.tactics.network.message.OperatorParameter;
import itti.com.pl.eda.tactics.network.message.TestCommand;
import itti.com.pl.eda.tactics.ontology.model.UserDataCache;
import itti.com.pl.eda.tactics.policy.impl.PolicyTripleImpl;


/**
 * interface specifies main pold functionality
 * @author marcin
 *
 */
public interface ILocalPoldInterface {


	/**
	 * stores new high-level policy in pold repository
	 * @param userId id of the policy author
	 * @param policyHL high-level policy object
	 * @param errors possible errors
	 * @return id of the policy if stored successfully, -1 if process fails
	 */
	public long storePolicy(long userId, PolicyHighLevel policyHL, List<String> errors);


	/**
	 * returns all high-level policies related to the user identified by id
	 * @param userId id of the user
	 * @param errors possible errors
	 * @return list of user high-level policies
	 */
	public List<PolicyHighLevel> getPoliciesHL(long userId, List<String> errors);


	/**
	 * returns single high-level policy by its name
	 * @param policyName name of the policy
	 * @param errors possible errors
	 * @return high-level policy if exists, or null otherwise
	 */
	public PolicyHighLevel getPolicyHL(String policyName, List<String> errors);


	/**
	 * returns single high-level policy by its id
	 * @param policyId id of the policy
	 * @param errors possible errors
	 * @return high-level policy if exists, or null otherwise
	 */
	public PolicyHighLevel getPolicyHL(long policyId, List<String> errors);


	/**
	 * deletes high-level policy from pold repository
	 * @param policyId id of the policy
	 * @param errors possible errors
	 * @return true, if policy was deleted, false otherwise
	 */
	public boolean deletePolicy(long policyId, List<String> errors);


	/**
	 * deletes all high-level policies from pold repository
	 * @param userId id of the policies author
	 * @param errors possible errors 
	 * @return true, if policies were deleted, false otherwise
	 */
	public boolean deleteUserPolicies(long userId, List<String> errors);


	/**
	 * replaces high-level policy with the new one
	 * @param policyId id of the old policy
	 * @param newPolicy new high-level policy
	 * @param errors possible errors
	 * @return true, if replace process ends with success, false otherwise
	 */
	public boolean replacePolicy(long policyId, PolicyHighLevel newPolicy, List<String> errors);


	/**
	 * returns intermediate-level policy from pold repository
	 * @param policyId id of the policy
	 * @param errors possible errors
	 * @return intermediate-level policy, or null if errors occurred
	 */
	public PolicyIntermediateLevel getPolicyIL(long policyId, List<String> errors);


	/**
	 * returns a list of intermediate-level policies from pold repository
	 * @param ipAddress id address of the policies
	 * @param application name of the application 
	 * @param policyType type of the policy (swrl-defined)
	 * @param errors possible errors
	 * @return list of intermediate-level policies or null if errors occurs
	 */
	public List<PolicyIntermediateLevel> getPoliciesIL(String ipAddress, String application, String policyType, List<String> errors);


	/**
	 * returns a list of intermediate-level policies from pold repository
	 * @param parentId id of the parent high-level policy
	 * @param errors possible errors
	 * @return list of intermediate-level policies or null if errors occurs
	 */
	public List<PolicyIntermediateLevel> getPoliciesILByParentId(long parentId, List<String> errors);


	/**
	 * returns a list of intermediate-level policies specified by various conditions
	 * @param ipAddress ip address
	 * @param application application
	 * @param policyType type of the policy
	 * @param errors possible errors
	 * @return list of PolicyIntermediateLevel objects
	 */
	public List<PolicyIntermediateLevel> getActivePoliciesIL(String ipAddress, String application, String policyType, List<String> errors);


	/**
	 * returns list of intermediate-level policies, which match specified criteria
	 * @param criteria policy selection criteria
	 * @param errors possible errors
	 * @return list of intermediate-level policies or null if errors occurs
	 */
	public List<PolicyIntermediateLevel> getPoliciesILByCriteria(PolicyTripleImpl criteria, List<String> errors);


	/**
	 * changes activation status of the intermediate-level policy
	 * @param policyId id of the policy
	 * @param active activation status
	 * @param errors possible errors
	 * @return true, if process ends with success, false otherwise
	 */
	public boolean setPolicyILActive(long policyId, boolean active, List<String> errors);


	/**
	 * loads all user data from database
	 * these data items are merged with ontology to update ontology in-memory model
	 * @param userId id of the user
	 * @param errors possible errors
	 * @return user related data objects, or null if process fails
	 */
	public UserDataCache getOntologyCache(long userId, List<String> errors);


	/**
	 * loads high-level policy names from pold repository related to the specified user
	 * @param userId id of the user
	 * @param errors possible errors
	 * @return all high-level policy names from pold repository
	 */
	public List<String> getHLPolicyNames(long userId, List<String> errors);


	/**
	 * initializes pold controller
	 * @param errors possible errors
	 * @return true, if initialization ends with success, false otherwise
	 */
	public boolean init(List<String> errors);

	/**
	 * disposes pold controller, releases all system resources
	 * @return true, if dispose process ends with success, false otherwise
	 */
	public boolean dispose();


	/**
	 * returns id of the pold user specified by his name
	 * @param userName name of the searched user
	 * @param errors possible errors
	 * @return id of the user, or -1 if user with specified name doesn't exist in pold repository
	 */
	public long getUserId(String userName, List<String> errors);


	/**
	 * overwrites default pold database access parameters specified in hibernate.config file
	 * @param host database host name
	 * @param user database user name
	 * @param password database access password
	 */
	public void setDBParameters(String host, String user, String password);


	/**
	 * returns pold status
	 * @return true. if pold service is active, false otherwise
	 */
	public boolean isActive();


	/**
	 * stores APM defined policy in the pold repository
	 * @param apmPolicy APM policy
	 * @param errors possible errors
	 * @return id of the related pold high-level policy if process ends with success, -1 otherwise
	 */
	public long storeAPMPolicy(IntermediateCommonServicePolicy apmPolicy,
			List<String> errors);


	/**
	 * performs pold ontology operator request
	 * @param operatorCommand request command
	 * @param argumentsMap map of request parameters
	 * @param errors possible errors
	 * @param object additional (in some cases) object
	 * @return operation result object
	 */
	public Object processOperatorRequest(OperatorCommands operatorCommand,
			Map<OperatorParameter, String> argumentsMap, List<String> errors, Object object);


	/**
	 * performs pold test request
	 * @param testCommand test command
	 * @param value additional parameter
	 * @param errors list of possible errors
	 * @return results, if any
	 */
	public Object processTestRequest(TestCommand testCommand,
			long value, List<String> errors);

	/**
	 * registers a new pold events listener object
	 * registered listener will be notified by pold about its all internal events
	 * like policy installed event etc...
	 * @param regAddress address of the listener
	 * @param errorList possible errors
	 * @return true, if registration ends with success, false otherwise
	 */
	public boolean registerListener(String regAddress, List<String> errorList);


	/**
	 * unregisters one of pold events listeners
	 * @param regAddress address of the listener
	 * @param errorList possible errors
	 * @return true, if process ends with success, false otherwise
	 */
	public boolean unregisterListener(String regAddress, List<String> errorList);
}
