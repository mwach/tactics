package itti.com.pl.eda.tactics.hibernate.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import itti.com.pl.eda.tactics.data.model.Network;
import itti.com.pl.eda.tactics.data.model.Ontology;
import itti.com.pl.eda.tactics.data.model.PolicyData;
import itti.com.pl.eda.tactics.data.model.PolicyHighLevel;
import itti.com.pl.eda.tactics.data.model.PolicyIntermediateLevel;
import itti.com.pl.eda.tactics.data.model.User;
import itti.com.pl.eda.tactics.data.translator.ApplicationTranslator;
import itti.com.pl.eda.tactics.data.translator.LocationTranslator;
import itti.com.pl.eda.tactics.data.translator.NetworkTranslator;
import itti.com.pl.eda.tactics.data.translator.PolicyTranslator;
import itti.com.pl.eda.tactics.data.translator.UserTranslator;
import itti.com.pl.eda.tactics.exception.PoldHibernateException;
import itti.com.pl.eda.tactics.exception.PoldHibernateException.PoldHibernateExceptionType;
import itti.com.pl.eda.tactics.network.message.OperatorCommands;
import itti.com.pl.eda.tactics.network.message.OperatorParameter;
import itti.com.pl.eda.tactics.operator.Application;
import itti.com.pl.eda.tactics.operator.Location;
import itti.com.pl.eda.tactics.operator.PolicyQoSLevel;
import itti.com.pl.eda.tactics.policy.impl.PolicyTripleImpl;
import itti.com.pl.eda.tactics.utils.StringUtils;


/**
 * class used to communication with database
 * @author marcin
 *
 */
public class HibernateWrapper {

	Logger logger = Logger.getLogger(HibernateWrapper.class);


	/**
	 * initialization - method checks that database service is active and ready to work
	 * @param hibernateAttr non-default db attributes
	 * @param errors to keep potential errors
	 * @return true, if database status is valid, false otherwise
	 * @throws PoldHibernateException
	 */
	public boolean init(Map<String, String> hibernateAttr, Collection<String> errors) throws PoldHibernateException {

		Session session = null;
		Transaction tx = null;
		boolean result = false;

		logger.info("POLD system initialization. Attempt to connect to the database");

		String query = "INSERT INTO system_startups VALUES(:date)";
		logger.debug(query);

		try{

			InitSessionFactory.setAttributes(hibernateAttr);

			session = InitSessionFactory.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			Date d = new Date();
			session.createSQLQuery(query).setDate("date", d).executeUpdate();

			tx.commit();
			result = true;

			logger.info("Database-Application connection OK");
		}catch(Exception e){
			saveException("init", e, errors);
			closeSession(tx, session);
			throw new PoldHibernateException(PoldHibernateExceptionType.InitializationException, e.toString());
		}
		return result;
	}


	/**
	 * closes database connection and release all reserved resources
	 */
	public void dispose() {
		logger.info("Disposing hibernate wrapper");
		InitSessionFactory.close();
	}


	/**
	 * loads default ontologies from database
	 * these ontologies are used during pold initialization
	 * @param errors potential errors are stored there
	 * @return list of ontologies
	 * @throws PoldHibernateException
	 */
	@SuppressWarnings("unchecked")
	public List<Ontology> getDefaultOntologies(List<String> errors) throws PoldHibernateException {

		List<Ontology> ontologies = null;

		Session session = null;

		logger.info("Loading default OWL ontologies from database");

		String query = "FROM Ontology WHERE default = :def";
		logger.debug(query);

		try{

			session = InitSessionFactory.getInstance().openSession();
			ontologies = session.createQuery(query).setBoolean("def", true).list();
			session.close();

			if(ontologies == null || ontologies.isEmpty()){
				throw new PoldHibernateException(PoldHibernateExceptionType.GetDefOntology, 
						"There are no ontologies marked as 'default' in POLD database");
			}

			logger.info("Loading ontogies: success. Number of ontologies: " + ontologies.size());

		}catch(Exception e){
			saveException("getDefaultOntologies", e, errors);
			closeSession(null, session);
			throw new PoldHibernateException(PoldHibernateExceptionType.GetDefOntology, e.toString());
		}
		return ontologies;
	}


	/**
	 * load ontology specified by its name
	 * @param ontologyName
	 * @param errors
	 * @return ontology object
	 */
	public Ontology getOntology(String ontologyName, Collection<String> errors) {

		Session session = null;

		logger.info("Loading OWL ontology from database with name: " + ontologyName);

		String query = "FROM Ontology WHERE ontologyName = :name";
		logger.debug(query);

		Ontology ontology = null;

		try{

			session = InitSessionFactory.getInstance().openSession();
			ontology = (Ontology)session.createQuery(query).setString("name", ontologyName).uniqueResult();
			session.close();

			if(ontology == null){
				throw new PoldHibernateException(PoldHibernateExceptionType.GetDefOntology, 
						"There are no ontology with given name in POLD database");
			}

			logger.info("Loading ontology: success. Loaded ontology id: " + ontology.getId());

		}catch(Exception e){
			saveException("getOntology: " + ontologyName, e, errors);
			closeSession(null, session);
			ontology = null;
		}
		return ontology;
	}


	/**
	 * loads defined qos classes used for refinement
	 * @param errors possible errors
	 * @return list of qos classes
	 * @throws PoldHibernateException
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getQoSClasses(Collection<String> errors) throws PoldHibernateException{

		List<Object[]> qosData = null;
		Session session = null;

		logger.info("Loading QoS parameters from database");

		String query = 
			"SELECT a.qos_class, a.qos_param, a.weight * b.weight AS weight , b.value " +
			"FROM qos_classes a INNER JOIN qos_criteria b ON a.qos_param = b.qos_param";

		logger.debug(query);

		try{

			session = InitSessionFactory.getInstance().openSession();

			qosData = session.createSQLQuery(query).list();

			session.close();

			if(qosData == null || qosData.isEmpty()){
				throw new PoldHibernateException(PoldHibernateExceptionType.GetQoSRefinementMap, 
						"There is no refinement data in the database");
			}

			logger.info("Refinement data successfully loaded");

		}catch(Exception e){
			saveException("getQoSClasses", e, errors);
			closeSession(null, session);
			throw new PoldHibernateException(PoldHibernateExceptionType.GetQoSRefinementMap);
		}
		return qosData;
	}


	/**
	 * loads user from database specified by his name
	 * @param userName name of the user
	 * @param errors possible errors
	 * @return user object or null if user with specified name doesn't exist in database
	 */
	public User loadUser(String userName, Collection<String> errors){

		User user = null;
		Session session = null;

		logger.debug("Loading info about user: " + userName);

		try{

			session = InitSessionFactory.getInstance().openSession();

			user = (User)session.createCriteria(User.class).add(Restrictions.eq("name", userName)).uniqueResult();

			session.close();

		}catch(Exception e){
			saveException("loadUser by name: " + userName, e, errors);
			closeSession(null, session);
		}

		return user;
	}


	/**
	 * loads user from database specified by his id
	 * @param userId id of the user
	 * @param errors possible errors
	 * @return user object or null if user with specified id doesn't exist in database
	 */
	public User loadUser(long userId, Collection<String> errors){

		User user = null;
		Session session = null;

		logger.debug("Loading info about user: " + userId);

		try{

			session = InitSessionFactory.getInstance().openSession();

			user = (User)session.load(User.class, userId);

			session.close();

		}catch(Exception e){
			saveException("loadUser by id: " + userId, e, errors);
			closeSession(null, session);
		}

		return user;
	}


	/**
	 * loads all intermediate-level policies from pold database
	 * method used for timeEvents thread
	 * @param errors possible errors
	 * @return list of all intermediate-level policies
	 */
	@SuppressWarnings("unchecked")
	public List<PolicyIntermediateLevel> loadAllIntermediateLevelPolicies(List<String> errors) {

		logger.debug("Loading all IL policies");

		Session session = null;
		List<PolicyIntermediateLevel> list = null;

		String query = "from PolicyIntermediateLevel";

		try{

			session = InitSessionFactory.getInstance().openSession();

			list = session.createQuery(query).list();

			session.close();

		}catch(Exception e){
			saveException("loadAllHighLevelPolicies", e, errors);
			closeSession(null, session);
		}

		return list;
	}


	/**
	 * removes from pold database high-level policy specified by its id or user id
	 * one of parameters should be set, second one should be set to -1 (it will be omitted)
	 * @param policyId policyId
	 * @param userId userId
	 * @param errors possible errors
	 * @return number of deleted policies
	 */
	@SuppressWarnings("unchecked")
	public int deletePolicy(long policyId, long userId, Collection<String> errors) {

		Session session = null;
		Transaction tx = null;

		int deletedCount = 0;

		logger.info("deleting policy with the id=" + policyId + ", userId=" + userId);

		try{
			if(policyId == -1 && userId == -1){
				throw new PoldHibernateException(PoldHibernateExceptionType.DeletePolicy, "policyId and userId are both not set");
			}

			session = InitSessionFactory.getInstance().openSession();

			tx = session.beginTransaction();

			if(policyId != -1){

				PolicyHighLevel phl = (PolicyHighLevel)session.load(PolicyHighLevel.class, policyId);
				if(phl != null){
					session.delete(phl);
					deletedCount = 1;
				}

			}else{

				User user = (User)session.load(User.class, userId);
				if(user != null){
					List<PolicyHighLevel> list = session.createCriteria(PolicyHighLevel.class).add(Restrictions.eq("author", user)).list();
					if(list != null && !list.isEmpty()){
						Iterator<PolicyHighLevel> phlIterator = list.iterator();
						while(phlIterator.hasNext()){
							PolicyHighLevel phl = phlIterator.next();
							if(phl != null){
								session.delete(phl);
								deletedCount++;
							}
						}
					}
				}
			}

			tx.commit();
			session.close();

			logger.info("Deleting policy ends with success. Policies deleted: " + deletedCount);

		}catch(Exception e){

			saveException("deletePolicy", e, errors);
			closeSession(tx, session);
		}
		return deletedCount;
	}


	/**
	 * replaces existing high-level policy with the new one
	 * @param policyId policy, which should be replaced
	 * @param newPolicy new high-level policy object
	 * @param errors possible errors
	 * @return true, if process ends with success, false otherwise
	 */
	public boolean replaceHLPolicy(long policyId, PolicyHighLevel newPolicy,
			Collection<String> errors) {

		logger.info("Replacing polic with the id: " + policyId);

		logger.debug("New policy data: " + newPolicy.toString());

		boolean result = false;

		Session session = null;
		Transaction tx = null;

		try{
			logger.debug("Replace policy: removing old policy");

			session = InitSessionFactory.getInstance().openSession();
			tx = session.beginTransaction();

			PolicyHighLevel phl = (PolicyHighLevel)session.load(PolicyHighLevel.class, policyId);
			if(phl != null){
				session.delete(phl);
				newPolicy.setId(-1);
				session.save(newPolicy);
			}

			tx.commit();
			session.close();

			session = InitSessionFactory.getInstance().openSession();
			tx = session.beginTransaction();

			session.createSQLQuery("update policies_hl set id = " + policyId + " where id = " + newPolicy.getId()).executeUpdate();
			session.createSQLQuery("update policies_il set hl_ref = " + policyId + " where hl_ref = " + newPolicy.getId()).executeUpdate();

			tx.commit();
			session.close();

			newPolicy.setId(policyId);

			logger.info("Replacing process ends with success. Policy with id: " + policyId + " was replaced with the new one");
			result = true;

		}catch(Exception e){
			saveException("Exception during replaceing HLPolicy with policyID" + policyId, e, errors);
			closeSession(tx, session);
		}

		return result;
	}


	/**
	 * loads intermediate-level policy from pold database specified by its id
	 * @param policyId id of the policy
	 * @param errors possible errors
	 * @return intermediate-level policy object
	 */
	public PolicyIntermediateLevel getILPolicy(long policyId, Collection<String> errors) {

		logger.info("Selecting IL policy with id: " + policyId);

		PolicyIntermediateLevel policy = null;

		Session session = null;

		try{
			session = InitSessionFactory.getInstance().openSession();

			policy = (PolicyIntermediateLevel)session.load(PolicyIntermediateLevel.class, policyId);

			session.close();

			logger.info("IL policy selecting process ends with no errors");

		}catch(Exception e){
			saveException("getILPolicy, policyId: " + policyId, e, errors);
			closeSession(null, session);
		}

		return policy;
	}


	/**
	 * returns list of intermediate-level policies from pold database
	 * ipAddress is required, also one of others parameters can be set, unused parameters should be set to null
	 * @param ipAddress id address
	 * @param application application name
	 * @param policyType policy type (defined by swrl rules)
	 * @param activeOnly all policies (flag set to false) or active only (flag set to true)
	 * @param errors possible errors
	 * @return list of intermediate-level policies
	 */
	@SuppressWarnings("unchecked")
	public List<PolicyIntermediateLevel> getILPolicies(String ipAddress, String application,
			String policyType, boolean activeOnly, Collection<String> errors) {

		logger.info("Selecting IL policies from database");

		String params = "ip=" + ipAddress + ", appliction=" + application + ", swrl type=" + policyType + ", active only:" + activeOnly;
		logger.info(params);

		String queryStr = 
			"FROM PolicyIntermediateLevel WHERE ipAddress  = :ipAddr";

		if(application != null){
			queryStr += " AND application = :app";
		}

		if(policyType != null){
			queryStr += " AND type = :type";
		}

		if(activeOnly){
			queryStr += " AND active = :state";
		}

		List<PolicyIntermediateLevel> returnList = null;

		Session session = null;

		try{
			session = InitSessionFactory.getInstance().openSession();
			Query query = session.createQuery(queryStr);

			query.setString("ipAddr", ipAddress);

			if(application != null){
				query.setString("app", application);
			}

			if(policyType != null){
				query.setString("type", policyType);
			}

			if(activeOnly){
				query.setBoolean("state", true);
			}

			returnList = query.list();

			session.close();

			logger.info("IL policies selecting process ends with success. Objects loaded: " + (returnList != null ? returnList.size() : "NULL"));

		}catch(Exception e){
			saveException("getILPolicies failed. Parameters used: " + params, e, errors);
			closeSession(null, session);
		}

		return returnList;
	}


	/**
	 * returns a list of intermediate-level policies from pold database
	 * @param parentPolicyId parent high-level policy id
	 * @param authorId policy author id
	 * @param errors possible errors
	 * @return list of intermediate-level policies
	 */
	@SuppressWarnings("unchecked")
	public List<PolicyIntermediateLevel> getILPoliciesByParentId(long parentPolicyId, long authorId, List<String> errors) {

		if(parentPolicyId != -1){
			logger.info("Selecting IL policies by parent policy ID = " + parentPolicyId + " from database");

		}else if(authorId != -1){
			logger.info("Selecting IL policies by author ID = " + authorId + " from database");

		}else{
			logger.warn("Selecting IL policies by parent ID: Both parameters (policyId and userId) are not set - nothing will be searched");
			return null;
		}

		String queryStr = 
			"FROM PolicyIntermediateLevel WHERE parent in ";

		if(parentPolicyId != -1){
			queryStr += "(FROM PolicyHighLevel WHERE id = :policyId)";

		//TODO: test if this condition works properly
		}else{
			queryStr += "(FROM PolicyHighLevel WHERE author = :userId)";
		}

		List<PolicyIntermediateLevel> returnList = null;

		Session session = null;

		try{
			session = InitSessionFactory.getInstance().openSession();
			Query query = session.createQuery(queryStr);

			if(parentPolicyId != -1){
				query.setLong("policyId", parentPolicyId);

			}else{
				query.setLong("userId", authorId);
			}

			returnList = query.list();

			session.close();

			logger.info("IL policies selecting process ends with success. Data length: " + (returnList != null ? returnList.size() : "NULL"));

		}catch(Exception e){
			saveException("getILPoliciesByParentId failed. Parent policy ID: " + parentPolicyId + ", author ID: " + authorId, e, errors);
			closeSession(null, session);
		}

		return returnList;
	}


	/**
	 * loads high-level policy from database
	 * one of parameters should be specified
	 * @param policyId policy id
	 * @param policyName policy name
	 * @param errors possible errors
	 * @return high-level policy object
	 */
	public PolicyHighLevel getHLPolicy(long policyId, String policyName, Collection<String> errors) {

		logger.info("Selecting HL policy with id: " + policyId + ", name: " + policyName);

		String queryStr = "FROM PolicyHighLevel WHERE ";

		if(policyId != -1){
			queryStr += "id = :policyId";
		}else{
			queryStr += "name = :policyName";
		}

		PolicyHighLevel policy = null;

		Session session = null;

		try{
			session = InitSessionFactory.getInstance().openSession();

			Query query = session.createQuery(queryStr);

			if(policyId != -1){
				query.setLong("policyId", policyId);
			}else{
				query.setString("policyName", policyName);
			}

			policy = (PolicyHighLevel)query.uniqueResult();

			session.close();

			logger.info("HL policy selecting process ends with success");

		}catch(Exception e){
			saveException("getHlPolicy failed. PolicyId: " + policyId + ", policyName: " + policyName, e, errors);
			closeSession(null, session);
		}

		return policy;
	}


	/**
	 * loads all high-level policies related to the specified user
	 * @param userId user id
	 * @param errors possible errors
	 * @return list of high-level policies
	 */
	@SuppressWarnings("unchecked")
	public List<PolicyHighLevel> getHLPolicies(long userId,
			Collection<String> errors) {

		logger.info("Selecting HL policies with user id: " + userId);

		List<PolicyHighLevel> retList = new ArrayList<PolicyHighLevel>();

		String queryStr = "FROM PolicyHighLevel";
		if(userId > 0){
			queryStr += " WHERE author = :userId";
		}

		Session session = null;

		try{
			session = InitSessionFactory.getInstance().openSession();
			Query query = session.createQuery(queryStr);

			if(userId > 0){
				query.setLong("userId", userId);
			}

			retList = query.list();

			session.close();
			logger.info("Selecting user policies ends with success. UserId: " + userId + ", policies loaded: " + (retList != null ? retList.size() : "NULL"));

		}catch(Exception e){
			saveException("getHLPolicies, userId: " + userId, e, errors);
			closeSession(null, session);
			cleanDatabase(userId);
			errors.add("Please login again");
		}

		return retList;
	}


	private void cleanDatabase(long userId) {

		logger.warn("DB CLEAINING MECHANIZM runned");

		List<String> errors = new ArrayList<String>();

		List<String> policies = getHLPolicyNames(userId, errors);

		if(policies != null && !policies.isEmpty()){

			for (String policyName : policies) {
				getHLPolicy(-1, policyName, errors);
				if(!errors.isEmpty()){
					Session session = null;
					Transaction trans = null;
					try{
						String deleteQuery = "delete from policies_hl where policy_name = '" + policyName + "'";
						session = InitSessionFactory.getInstance().openSession();
						trans = session.beginTransaction();
						session.createSQLQuery(deleteQuery).executeUpdate();
						trans.commit();
						session.close();
					}catch (Exception e) {
						closeSession(trans, session);
						e.printStackTrace();
					}
					errors.clear();
				}
			}
		}
	}


	/**
	 * loads names of all high-level policies related to the specified user
	 * @param userId user id
	 * @param errors possible errors
	 * @return list of high-level policy names
	 */
	@SuppressWarnings("unchecked")
	public List<String> getHLPolicyNames(long userId,
			Collection<String> errors) {

		logger.info("Selecting HL policy names with user id: " + userId);

		List<String> retList = new ArrayList<String>();

		String queryStr = "SELECT policy_name FROM policies_hl WHERE author_id = ";
		queryStr += userId;

		Session session = null;

		try{
			session = InitSessionFactory.getInstance().openSession();
			Query query = session.createSQLQuery(queryStr);

			retList = query.list();

			session.close();
			logger.info("Selecting user policy names ends with success. UserId: " + userId + ", policies loaded: " + (retList != null ? retList.size() : "NULL"));

		}catch(Exception e){
			saveException("getHLPolicies, userId: " + userId, e, errors);
			closeSession(null, session);
		}

		return retList;
	}


	/**
	 * stores new high-level policy in pold database
	 * @param policy hilgh-level policy
	 * @param errors possible errors
	 * @return id of this policy if stored successfully, -1 when process fails
	 */
	public long storeHLPolicy(PolicyHighLevel policy, Collection<String> errors) {

		logger.info("Storing HL policy in the database: " + policy.getName());
		logger.debug(policy.toString());

		Session session = null;
		Transaction tx = null;

		long result = -1;

		try{
			session = InitSessionFactory.getInstance().openSession();
			tx = session.beginTransaction();

			session.save(policy);

			tx.commit();
			session.close();

			logger.info("Storing process ends with success. Id of the new policy: " + policy.getId());
			result = policy.getId();

		}catch(Exception e){
			saveException("storeHLPolicy failed : " + policy.toString(), e, errors);
			closeSession(tx, session);
			result = -1;
		}
		return result;
	}


	/**
	 * updates state of intermediate-level policy (active or not)
	 * @param policyId id of intermediate-level policy
	 * @param state new state (active or not)
	 * @param errors possible errors
	 * @return true, if process ends with success, false otherwise
	 */
	public boolean changeILPolicyState(long policyId, boolean state, Collection<String> errors) {

		logger.info("Changing IL policy state. policyId: " + policyId + ", new state: " + state);

		boolean result = false;

		Session session = null;
		Transaction tx = null;

		try{
			session = InitSessionFactory.getInstance().openSession();
			tx = session.beginTransaction();

			PolicyIntermediateLevel policy = (PolicyIntermediateLevel) session.load(PolicyIntermediateLevel.class, policyId);

			if(policy != null){
				policy.setActive(state);
			}else{
				throw new PoldHibernateException(PoldHibernateExceptionType.PolicyActivate, "Policy with ID: " + policyId + " not found");
			}

			tx.commit();
			session.close();

			logger.info("Status changing ends with success. ILPolicyId: " + policyId + ", new state: " + state);
			result = true;

		}catch(Exception e){
			saveException("changeILPolicyState failed. ILPolicyId: " + policyId, e, errors);
			closeSession(tx, session);
		}
		return result;
	}


	/**
	 * loads intermediate-level policies, which match specified criteria pattern
	 * @param criteria policy criteria
	 * @param errors possible errors
	 * @return list of intermediate-level policies matching criteria
	 */
	@SuppressWarnings("unchecked")
	public List<PolicyIntermediateLevel> getILPoliciesByCriteria(PolicyTripleImpl criteria, List<String> errors) {

		logger.info("Loading IL policies using criteria");

		Map<String, String> arguments = new HashMap<String, String>();
		List<PolicyIntermediateLevel> retList = null;


		String queryStr = criteria.getSqlQuery(arguments);

		Session session = null;

		logger.debug("Criteria:\n" + criteria);

		try{
			session = InitSessionFactory.getInstance().openSession();
			Query query = session.createQuery(queryStr);
			for (String parameter : arguments.keySet()) {
				String val = arguments.get(parameter);
				if(StringUtils.isInt(val)){
					query.setInteger(parameter, Integer.parseInt(val.trim()));
				}else{
					query.setString(parameter, val);
				}
			}

			retList = query.list();

			session.close();

		}catch(Exception e){
			saveException("Exception during selecting ILPolicies by criteria", e, errors);
			closeSession(null, session);
			retList = null;
		}

		return retList;
	}


	@SuppressWarnings("unchecked")
	public long getNumberOfILPolicies(Collection<String> errors){

		logger.info("Getting an IL ontology size");

		Session session = null;

		String query = "SELECT count(*) FROM policies_il";
		long count = 0;

		try{
			session = InitSessionFactory.getInstance().openSession();
			List<Object> result = session.createSQLQuery(query).list();
			if(!result.isEmpty()){
				count = Long.parseLong(result.get(0).toString());
			}

			session.close();

			logger.info("Searching results size: " + count);

		}catch(Exception e){
			saveException("Exception during calculating reposiory size", e, errors);
			closeSession(null, session);
		}

		return count;
	}


	/**
	 * loads from database ontology data related to the user identified by his id
	 * this data will be merged with ontology to update in-memory ontology model
	 * @param userId id of the user
	 * @param errors possible errors
	 * @return list of user ontology data
	 */
	//TODO: user id is not used!
	public List<PolicyData> getUserRelatedOntologyData(long userId, Collection<String> errors) {

		logger.info("Loading user data from database. User id: " + userId);

		User user = loadUser(userId, errors);

		List<PolicyData> policyData = new ArrayList<PolicyData>();

		if(user != null && user.getLocations() != null){
			Set<itti.com.pl.eda.tactics.data.model.Location> locations = user.getLocations();
			for (itti.com.pl.eda.tactics.data.model.Location location : locations) {
				//qos ontology
				PolicyData pdQoS = new PolicyData(location.getQosClass(), location.getLocationName() + "_qos", true);
				//policy ontology
				PolicyData pd = new PolicyData("LocalizationValue", location.getLocationName(), true);
				pd.addProperty("Localization_Value_has_Value", location.getLocationName() + "_qos");

				//networks
				if(location.getNetworks() != null){
					for (String netIp : location.getNetworks().keySet()) {
						Network network = location.getNetworks().get(netIp);
						String qosLevelName = "qos_level_" + network.getName() + "_" + netIp;

						PolicyData delay = new PolicyData("QoSParameter_Delay", "Delay_" + netIp, true);
						delay.addProperty("QoSParameter-hasValue", "" + network.getDelay());
						policyData.add(delay);
						PolicyData jitter = new PolicyData("QoSParameter_Jitter", "Jitter_" + netIp, true);
						jitter.addProperty("QoSParameter-hasValue", "" + network.getJitter());
						policyData.add(jitter);
						PolicyData loss = new PolicyData("QoSParameter_Loss", "Loss_" + netIp, true);
						loss.addProperty("QoSParameter-hasValue", "" + network.getLoss());
						policyData.add(loss);
						PolicyData throughput = new PolicyData("QoSParameter_Throughput", "Throughput_" + netIp, true);
						throughput.addProperty("QoSParameter-hasValue", "" + network.getBandwidth());
						policyData.add(throughput);

						PolicyData qosLevel = new PolicyData("CoS", qosLevelName, true);
						qosLevel.addProperty("QoSLevel-hasQoSParameter", "Delay_" + netIp);
						qosLevel.addProperty("QoSLevel-hasQoSParameter", "Jitter_" + netIp);
						qosLevel.addProperty("QoSLevel-hasQoSParameter", "Loss_" + netIp);
						qosLevel.addProperty("QoSLevel-hasQoSParameter", "Throughput_" + netIp);
						qosLevel.addProperty("QoSLevel-hasQoSParameter", "Reliability_HIG");
						policyData.add(qosLevel);

						String accessNetworkName = network.getName();
						PolicyData net = new PolicyData("AccessNetwork", accessNetworkName, true);
						net.addProperty("Resource-hasOwner", user.getName());
						net.addProperty("Network-hasCos_Support", qosLevelName);
						net.addProperty("Network-hasIPAddressRange", netIp);
						policyData.add(net);

						pdQoS.addProperty("Location-hasNetworkSupport", accessNetworkName);
					}
				}
				policyData.add(pdQoS);
				policyData.add(pd);
			}
		}

		List<PolicyQoSLevel> policyLevels = operatorLoadPolicyQoSLevels(errors);
		if(policyLevels != null){

			String policyHasQoSPropertyName = "QoSPolicyLevel-isRelatedtoRealQoSLevel";
			String policyClassName = "QoSReqValue";
			String qosParametersPropertyName = "QoSLevel-hasQoSParameter";
			String qosLevelClassName = "QoS_Level_PolicyActionVar";
			String qosParameterValuePropertyName = "QoSParameter-hasValue";

			for (PolicyQoSLevel policyQoSLevel : policyLevels) {

				String policyPropName = policyQoSLevel.getName();
				String policyPropQoSName = policyQoSLevel.getQosName();

				String qosInstanceBandwidth = "Bandwidth_" + policyQoSLevel.getBandwidth();
				String qosInstanceDelay = "Delay_" + policyQoSLevel.getDelay();
				String qosInstanceJitter = "Jitter_" + policyQoSLevel.getJitter();
				String qosInstanceLoss = "Loss_" + policyQoSLevel.getLoss();

				PolicyData pdQoS1= new PolicyData("QoSParameter_Delay", qosInstanceDelay, true);
				pdQoS1.addProperty(qosParameterValuePropertyName, "" + policyQoSLevel.getDelay());
				PolicyData pdQoS2= new PolicyData("QoSParameter_Jitter", qosInstanceJitter, true);
				pdQoS2.addProperty(qosParameterValuePropertyName, "" + policyQoSLevel.getJitter());
				PolicyData pdQoS3= new PolicyData("QoSParameter_Loss", qosInstanceLoss, true);
				pdQoS3.addProperty(qosParameterValuePropertyName, "" + policyQoSLevel.getLoss());
				PolicyData pdQoS4= new PolicyData("QoSParameter_Throughput", qosInstanceBandwidth, true);
				pdQoS4.addProperty(qosParameterValuePropertyName, "" + policyQoSLevel.getBandwidth());

				policyData.add(pdQoS1);
				policyData.add(pdQoS2);
				policyData.add(pdQoS3);
				policyData.add(pdQoS4);

				PolicyData policyQoS = new PolicyData(qosLevelClassName, policyPropQoSName, true);
				policyQoS.addProperty(qosParametersPropertyName, qosInstanceBandwidth);
				policyQoS.addProperty(qosParametersPropertyName, qosInstanceDelay);
				policyQoS.addProperty(qosParametersPropertyName, qosInstanceJitter);
				policyQoS.addProperty(qosParametersPropertyName, qosInstanceLoss);
				policyData.add(policyQoS);

				PolicyData policyInstance = new PolicyData(policyClassName, policyPropName, true);
				policyInstance.addProperty(policyHasQoSPropertyName, policyPropQoSName);
				policyData.add(policyInstance);
			}
		}

		List<Application> applications = operatorLoadApplications(errors);
		if(applications != null){
			for (Application application : applications) {

				String qosInstanceBandwidth = "Bandwidth_" + application.getBandwidth();
				String qosInstanceDelay = "Delay_" + application.getDelay();
				String qosInstanceJitter = "Jitter_" + application.getJitter();
				String qosInstanceLoss = "Loss_" + application.getLoss();

				PolicyData pdQoS1= new PolicyData("QoSParameter_Delay", qosInstanceDelay, true);
				pdQoS1.addProperty("QoSParameter-hasValue", "" + application.getDelay());
				PolicyData pdQoS2= new PolicyData("QoSParameter_Jitter", qosInstanceJitter, true);
				pdQoS2.addProperty("QoSParameter-hasValue", "" + application.getJitter());
				PolicyData pdQoS3= new PolicyData("QoSParameter_Loss", qosInstanceLoss, true);
				pdQoS3.addProperty("QoSParameter-hasValue", "" + application.getLoss());
				PolicyData pdQoS4= new PolicyData("QoSParameter_Throughput", qosInstanceBandwidth, true);
				pdQoS4.addProperty("QoSParameter-hasValue", "" + application.getBandwidth());

				policyData.add(pdQoS1);
				policyData.add(pdQoS2);
				policyData.add(pdQoS3);
				policyData.add(pdQoS4);

				PolicyData appQoS = new PolicyData("NetworkApplication", application.getName() + "_qos", true);
				appQoS.addProperty("Application_has_QoSParameters", qosInstanceBandwidth);
				appQoS.addProperty("Application_has_QoSParameters", qosInstanceDelay);
				appQoS.addProperty("Application_has_QoSParameters", qosInstanceJitter);
				appQoS.addProperty("Application_has_QoSParameters", qosInstanceLoss);
				policyData.add(appQoS);

				PolicyData appPolicy = new PolicyData("ApplicationValue", application.getName(), true);
				appPolicy.addProperty("Application_Value_has_Value", application.getName() + "_qos");
				policyData.add(appPolicy);

				PolicyData pdType = new PolicyData(application.getType(), application.getName() + "_type", true);
				policyData.add(pdType);
			}
		}

		return policyData;
	}


	/**
	 * method used for ontology operator requests
	 * based on request parameters this method invokes proper database operation
	 * @param operatorCommand operator command
	 * @param argumentsMap list of arguments
	 * @param errors possible errors
	 * @param object additional (in some cases) object used during request
	 * @return object (dependent on request parameters)
	 */
	public Object processOperatorRequest(OperatorCommands operatorCommand,
			Map<OperatorParameter, String> argumentsMap, List<String> errors, Object object) {

		logger.info("Executing operator request: " + operatorCommand);

		logger.debug("Arguments: " + argumentsMap);
		logger.debug("Object: " + object);

		switch (operatorCommand) {

			case AddUser:
				return operatorAddUser(argumentsMap, errors);
			case DeleteUser:
				return operatorDeleteUser(argumentsMap, errors);
			case UsersList:
				return operatorGetSimpleList(errors, "name", "users");
			case LoadUser:
				return operatorLoadUser(argumentsMap, errors);

			case LocationsList:
				return operatorGetSimpleList(errors, "location_name", "locations");

			case AddLocationToTheUser:
				return operatorAddLocationToTheUser(argumentsMap, errors);

			case RemoveLocationFromTheUser:
				return operatorRemoveLocationFromTheUser(argumentsMap, errors);

			case NetworksList:
				return operatorGetSimpleList(errors, "name", "networks");

			case AddLocation:
				return operatorAddLocation(errors, object);

			case UpdateLocation:
				return operatorUpdateLocation(errors, object);

			case LoadLocation:
				return operatorLoadLocation(argumentsMap, errors);

			case DeleteLocation:
				return operatorDeleteLocation(argumentsMap, errors);

			case AddNetwork:
				return operatorAddNetwork(object, errors);
			case UpdateNetwork:
				return operatorUpdateNetwork(object, errors);
			case LoadNetwork:
				return operatorLoadNetwork(argumentsMap, errors);
			case RemoveNetwork:
				return operatorDeleteNetwork(argumentsMap, errors);

			case ApplicationList:
				return operatorGetSimpleList(errors, "name", "applications");
			case AddApplication:
				return operatorAddApplication(object, errors);
			case DeleteApplication:
				return operatorDeleteApplication(argumentsMap, errors);
			case LoadApplication:
				return operatorLoadApplication(argumentsMap, errors);
			case UpdateApplication:
				return operatorUpdateApplication(object, errors);

			case QoSPoliciesList:
				return operatorGetSimpleList(errors, "name", "qos_levels_policy");
			case AddQoSPolicy:
				return operatorAddQoSPolicy(object, errors);
			case DeleteQoSPolicy:
				return operatorDeleteQoSPolicy(argumentsMap, errors);
			case LoadQoSPolicy:
				return operatorLoadQoSPolicy(argumentsMap, errors);
			case UpdateQoSPolicy:
				return operatorUpdateQoSPolicy(object, errors);

			default:
				return -1;
		}
	}


	private long operatorAddUser(Map<OperatorParameter, String> argumentsMap, List<String> errors) {

		String userName = argumentsMap.get(OperatorParameter.Name);

		if(operatorFindUserId(userName, errors) != -1){
			errors.add("User with name: " + userName + " already exists");
			return -1;
		}
		if(!errors.isEmpty()){
			return -1;
		}

		User user = new User();
		user.setName(userName);

		Session session = null;
		Transaction tx = null;

		logger.info("Adding a new pold user: " + userName);

		try{
			session = InitSessionFactory.getInstance().openSession();
			tx = session.beginTransaction();

			session.save(user);

			tx.commit();
			session.close();

			logger.info("Adding process ends with success. Generated userId: " + user.getId());

		}catch(Exception e){
			saveException("Exception during adding new pold user : " + userName, e, errors);
			closeSession(tx, session);
		}

		return user.getId();
	}


	private long operatorDeleteUser(Map<OperatorParameter, String> argumentsMap, List<String> errors) {

		String userName = argumentsMap.get(OperatorParameter.Name);

		User user = null;

		Session session = null;
		Transaction tx = null;

		logger.info("Deleting pold user: " + userName);
		long userId = operatorFindUserId(userName, errors);
		logger.info("pold user id: " + userId);

		if(userId != -1){
			try{
				session = InitSessionFactory.getInstance().openSession();
				tx = session.beginTransaction();

				user = (User)session.load(User.class, userId);
				session.delete(user);

				tx.commit();
				session.close();

				logger.info("Deleting process ends with success. Deleted user with userId: " + user.getId());

			}catch(Exception e){
				saveException("Exception during deleting pold user : " + userName, e, errors);
				closeSession(tx, session);
			}
		}
		return userId;
	}


	private boolean operatorDeleteLocation(Map<OperatorParameter, String> argumentsMap, List<String> errors) {

		String locationName = argumentsMap.get(OperatorParameter.Name);

		String usersToLocationQuery = "delete from USERS_TO_LOCATIONS where LOCATION_ID = ";

		Session session = null;
		Transaction tx = null;

		logger.info("Deleting location: " + locationName);
		long locationId = operatorFindLocationId(locationName, errors);
		logger.info("pold location id: " + locationId);

		if(locationId != -1){
			try{
				session = InitSessionFactory.getInstance().openSession();
				tx = session.beginTransaction();

//				location = (Location)session.load(Location.class, locationId);
//				session.delete(location);
				String delete1 = "delete from locations where id=" + locationId;
				session.createSQLQuery(delete1).executeUpdate();

				usersToLocationQuery += locationId;
				session.createSQLQuery(usersToLocationQuery).executeUpdate();

				tx.commit();
				session.close();

				logger.info("Deleting process ends with success. Deleted location with id: " + locationId);

			}catch(Exception e){
				saveException("Exception during deleting location : " + locationName, e, errors);
				closeSession(tx, session);
			}
		}
		return errors.isEmpty();
	}


	private boolean operatorDeleteApplication(Map<OperatorParameter, String> argumentsMap, List<String> errors) {

		String applicationName = argumentsMap.get(OperatorParameter.Name);

		Session session = null;
		Transaction tx = null;

		logger.info("Deleting application: " + applicationName);

		try{
			session = InitSessionFactory.getInstance().openSession();
			tx = session.beginTransaction();

			long id = -1;
			Application app = (Application)session.createCriteria(Application.class).add(Restrictions.eq("name", applicationName)).uniqueResult();
			if(app != null){
				id = app.getId();
				session.delete(app);
			}

			tx.commit();
			session.close();

			logger.info("Deleting process ends with success. Deleted application with id: " + id);

		}catch(Exception e){
			saveException("Exception during deleting application : " + applicationName, e, errors);
			closeSession(tx, session);
		}
		return errors.isEmpty();
	}


	private long operatorFindUserId(String userName, List<String> errors){

		User user = null;
		long result = -1;

		if(userName == null || userName.trim().equals("")){
			errors.add("User name is null or empty");
			return result;
		}

		Session session = null;

		logger.info("Searching pold user: " + userName);

		try{
			session = InitSessionFactory.getInstance().openSession();

			user = (User)session.createCriteria(User.class).add(Restrictions.eq("name", userName)).uniqueResult();
			if(user != null){
				result = user.getId();
			}

			session.close();

			logger.info("Searching results: " + result);

		}catch(Exception e){
			saveException("Exception during searching for a pold user : " + userName, e, errors);
			closeSession(null, session);
		}

		return result;
	}


	private long operatorFindLocationId(String location, List<String> errors){

		Location loc = null;
		long result = -1;

		if(location == null || location.trim().equals("")){
			errors.add("Location name is null or empty");
			return result;
		}

		Session session = null;

		logger.info("Searching location: " + location);

		try{
			session = InitSessionFactory.getInstance().openSession();

			loc = (Location)session.createCriteria(Location.class).add(Restrictions.eq("locationName", location)).uniqueResult();
			if(loc != null){
				result = loc.getId();
			}

			session.close();

			logger.info("Searching results: " + result);

		}catch(Exception e){
			saveException("Exception during searching for a pold location : " + location, e, errors);
			closeSession(null, session);
		}

		return result;
	}


	private Network operatorFindNetwork(String network, List<String> errors){

		Network net = null;

		if(network == null || network.trim().equals("")){
			errors.add("Network name is null or empty");
			return null;
		}

		Session session = null;

		logger.info("Searching network: " + network);

		try{
			session = InitSessionFactory.getInstance().openSession();

			net = (Network)session.createCriteria(Network.class).add(Restrictions.eq("name", network)).uniqueResult();

			session.close();

			logger.info("Searching results: " + net);

		}catch(Exception e){
			saveException("Exception during searching for a pold network : " + network, e, errors);
			closeSession(null, session);
		}

		return net;
	}


	private Application operatorFindApplication(String appName, List<String> errors){

		Application app = null;

		if(appName == null || appName.trim().equals("")){
			errors.add("Network name is null or empty");
			return null;
		}

		Session session = null;

		logger.info("Searching application: " + appName);

		try{
			session = InitSessionFactory.getInstance().openSession();

			app = (Application)session.createCriteria(Application.class).add(Restrictions.eq("name", appName)).uniqueResult();

			session.close();

			logger.info("Searching results: " + app);

		}catch(Exception e){
			saveException("Exception during searching for a pold application : " + appName, e, errors);
			closeSession(null, session);
		}

		return app;
	}

	private PolicyQoSLevel operatorFindQoSPolicy(String policyName, List<String> errors){

		PolicyQoSLevel policy = null;

		if(policyName == null || policyName.trim().equals("")){
			errors.add("Policy QoS name is null or empty");
			return null;
		}

		Session session = null;

		logger.info("Searching policy qos: " + policyName);

		try{
			session = InitSessionFactory.getInstance().openSession();

			policy = (PolicyQoSLevel)session.createCriteria(PolicyQoSLevel.class).add(Restrictions.eq("name", policyName)).uniqueResult();

			session.close();

			logger.info("Searching results: " + policy);

		}catch(Exception e){
			saveException("Exception during searching for a pold qos policy: " + policyName, e, errors);
			closeSession(null, session);
		}

		return policy;
	}

	@SuppressWarnings("unchecked")
	private List<String> operatorGetSimpleList(Collection<String> errors, String selectItem, String selectTable){

		logger.info("Selecting list of elements");
		logger.info("Items " + selectItem + " from table " + selectTable);

		Session session = null;

		String query = "SELECT " + selectItem + " FROM " + selectTable;

		List<String> list = null;

		try{
			session = InitSessionFactory.getInstance().openSession();
			list = session.createSQLQuery(query).list();

			session.close();

			logger.info("Searching results size: " + list.size());

		}catch(Exception e){
			saveException("Exception during selecting list", e, errors);
			closeSession(null, session);
		}

		return list;
	}


	private itti.com.pl.eda.tactics.operator.User operatorLoadUser(Map<OperatorParameter, String> params, List<String> errors){

		logger.info("Selecting user locations list");

		String userName = params.get(OperatorParameter.Name);

		long userId = operatorFindUserId(userName, errors);

		if(userId == -1){
			return null;

		}else{

			Session session = null;
			User user = null;

			try{
				session = InitSessionFactory.getInstance().openSession();

				user = (User)session.load(User.class, userId);

				session.close();

				if(user != null){
					logger.info("User data loaded: " + user.toString());
				}

			}catch(Exception e){
				saveException("Exception during selecting pold users list", e, errors);
				closeSession(null, session);
			}
			return UserTranslator.getOperatorUser(user);
		}
	}


	private Location operatorLoadLocation(Map<OperatorParameter, String> params, List<String> errors){

		logger.info("Selecting location");

		String locationName = params.get(OperatorParameter.Name);

		long locationId = operatorFindLocationId(locationName, errors);

		if(locationId == -1){
			return null;

		}else{

			Session session = null;
			itti.com.pl.eda.tactics.data.model.Location location = null;

			try{
				session = InitSessionFactory.getInstance().openSession();

				location = (itti.com.pl.eda.tactics.data.model.Location)session.load(itti.com.pl.eda.tactics.data.model.Location.class, locationId);

				session.close();

				if(location != null){
					logger.info("Location data loaded: " + location.toString());
				}

			}catch(Exception e){
				saveException("Exception during selecting pold users list", e, errors);
				closeSession(null, session);
			}
			return LocationTranslator.getLocation(location);
		}
	}


	private boolean operatorAddLocationToTheUser(Map<OperatorParameter, String> argumentsMap, List<String> errors) {

		String userName = argumentsMap.get(OperatorParameter.Name);
		String locationName = argumentsMap.get(OperatorParameter.Location);

		long userId = operatorFindUserId(userName, errors);
		if(userId == -1){
			errors.add("User with name: " + userName + " doesn't exists");
			return false;
		}

		long locationId = operatorFindLocationId(locationName, errors);
		if(locationId == -1){
			errors.add("Location: " + locationName + " doesn't exists");
			return false;
		}

		Session session = null;
		Transaction tx = null;

		logger.info("Loading pold user: " + userName);

		try{
			session = InitSessionFactory.getInstance().openSession();
			tx = session.beginTransaction();

			User user = (User)session.load(User.class, userId);
			itti.com.pl.eda.tactics.data.model.Location location = (itti.com.pl.eda.tactics.data.model.Location)session.load(itti.com.pl.eda.tactics.data.model.Location.class, locationId);

			user.getLocations().add(location);

			session.save(user);

			tx.commit();
			session.close();

			logger.info("Adding process ends with success. Generated userId: " + user.getId());

		}catch(Exception e){
			saveException("Exception during adding new pold user : " + userName, e, errors);
			closeSession(tx, session);
		}

		return errors.isEmpty();
	}


	private boolean operatorRemoveLocationFromTheUser(Map<OperatorParameter, String> argumentsMap, List<String> errors) {

		String userName = argumentsMap.get(OperatorParameter.Name);
		String locationName = argumentsMap.get(OperatorParameter.Location);

		long userId = operatorFindUserId(userName, errors);
		if(userId == -1){
			errors.add("User with name: " + userName + " doesn't exists");
			return false;
		}

		long locationId = operatorFindLocationId(locationName, errors);
		if(locationId == -1){
			errors.add("Location: " + locationName + " doesn't exists");
			return false;
		}

		Session session = null;
		Transaction tx = null;

		logger.info("Loading pold user: " + userName);

		try{
			session = InitSessionFactory.getInstance().openSession();
			tx = session.beginTransaction();

			User user = (User)session.load(User.class, userId);
			Location location = (Location)session.load(Location.class, locationId);

			user.getLocations().remove(location);

			session.save(user);

			tx.commit();
			session.close();

			logger.info("Adding process ends with success. Generated userId: " + user.getId());

		}catch(Exception e){
			saveException("Exception during adding new pold user : " + userName, e, errors);
			closeSession(tx, session);
		}

		return errors.isEmpty();
	}


	private long operatorAddLocation(List<String> errors, Object object) {

		if(object != null){
			Location locationObj = (Location)object;

			Map<String, Network> networks = new HashMap<String, Network>();
			for (String netIp : locationObj.getNetworks().keySet()) {
				String netName = locationObj.getNetworks().get(netIp);
				Network network = operatorFindNetwork(netName, errors);
				networks.put(netIp, network);
			}
			itti.com.pl.eda.tactics.data.model.Location location = LocationTranslator.getLocation(locationObj, networks);

			if(operatorFindLocationId(location.getLocationName(), errors) != -1){
				errors.add("Location with name: " + location.getLocationName() + " already exists");
				return -1;
			}

			if(!errors.isEmpty()){
				return -1;
			}

			Session session = null;
			Transaction tx = null;

			logger.info("Adding a new location: " + location.getLocationName());

			try{
				session = InitSessionFactory.getInstance().openSession();
				tx = session.beginTransaction();

				session.save(location);

				tx.commit();
				session.close();

				logger.info("Adding process ends with success. Generated locationId: " + location.getId());

			}catch(Exception e){
				saveException("Exception during adding new location : " + location.getLocationName(), e, errors);
				closeSession(tx, session);
			}

			return location.getId();
		}

		return -1;
	}


	private long operatorUpdateLocation(List<String> errors, Object object) {

		long locId = -1;

		if(object != null){
			Location locationObj = (Location)object;

			Map<String, Network> networks = new HashMap<String, Network>();
			for (String netIp : locationObj.getNetworks().keySet()) {
				String netName = locationObj.getNetworks().get(netIp);
				Network network = operatorFindNetwork(netName, errors);
				networks.put(netIp, network);
			}

			itti.com.pl.eda.tactics.data.model.Location location = LocationTranslator.getLocation(locationObj, networks);

			if(operatorFindLocationId(location.getLocationName(), errors) == -1){
				return operatorAddLocation(errors, object);
			}

			if(!errors.isEmpty()){
				return -1;
			}

			Session session = null;
			Transaction tx = null;

			logger.info("Updating an existing location: " + location.getLocationName());

			try{
				session = InitSessionFactory.getInstance().openSession();
				tx = session.beginTransaction();

				itti.com.pl.eda.tactics.data.model.Location currLocation = (itti.com.pl.eda.tactics.data.model.Location)session.createCriteria(Location.class).add(Restrictions.eq("locationName", location.getLocationName())).uniqueResult();

				locId = currLocation.getId();
				currLocation.setQosClass(location.getQosClass());

				currLocation.setNetworks(networks);

				session.save(currLocation);

				tx.commit();
				session.close();

				logger.info("Adding process ends with success. Generated locationId: " + location.getId());

			}catch(Exception e){
				saveException("Exception during adding new location : " + location.getLocationName(), e, errors);
				closeSession(tx, session);
			}

			return locId;
		}

		return -1;
	}


	private long operatorAddNetwork(Object object, List<String> errors) {

		if(object != null){

			itti.com.pl.eda.tactics.operator.Network netNetwork = (itti.com.pl.eda.tactics.operator.Network)object;
			Network network = NetworkTranslator.getNetwork(netNetwork);

			String networkName = network.getName();
			Network localNetwork = operatorFindNetwork(networkName, errors);

			if(localNetwork != null){
				errors.add("Network with name: " + networkName + " already exists");
				return -1;
			}

			if(!errors.isEmpty()){
				return -1;
			}

			Session session = null;
			Transaction tx = null;

			logger.info("Adding a new network: " + networkName);

			try{
				session = InitSessionFactory.getInstance().openSession();
				tx = session.beginTransaction();

				session.save(network);

				tx.commit();
				session.close();

				logger.info("Adding process ends with success. Generated locationId: " + network.getId());

			}catch(Exception e){
				saveException("Exception during adding new location : " + network.getName(), e, errors);
				closeSession(tx, session);
			}

			return network.getId();
		}

		return -1;
	}


	private long operatorUpdateNetwork(Object object, List<String> errors) {

		if(object != null){

			itti.com.pl.eda.tactics.operator.Network netNetwork = (itti.com.pl.eda.tactics.operator.Network)object;
			Network network = NetworkTranslator.getNetwork(netNetwork);

			String networkName = network.getName();
			Network localNetwork = operatorFindNetwork(networkName, errors);

			if(localNetwork == null){
				errors.add("Network with name: " + networkName + " does not exist");
				return -1;
			}

			if(!errors.isEmpty()){
				return -1;
			}

			Session session = null;
			Transaction tx = null;

			logger.info("Modyfing a new network: " + networkName);

			long localNetworkId = localNetwork.getId();

			try{
				session = InitSessionFactory.getInstance().openSession();
				tx = session.beginTransaction();

				Network net = (Network)session.load(Network.class, localNetworkId);

				net.setBandwidth(network.getBandwidth());
				net.setJitter(network.getJitter());
				net.setLoss(network.getLoss());
				net.setDelay(network.getDelay());

				net.setCondBandwidth(network.getCondBandwidth());
				net.setCondJitter(network.getCondJitter());
				net.setCondLoss(network.getCondLoss());
				net.setCondDelay(network.getCondDelay());

				session.save(net);

				tx.commit();
				session.close();

				logger.info("Adding process ends with success. Generated locationId: " + network.getId());

			}catch(Exception e){
				saveException("Exception during adding new location : " + network.getName(), e, errors);
				closeSession(tx, session);
			}

			return localNetworkId;
		}

		return -1;
	}


	private long operatorUpdateApplication(Object object, List<String> errors) {

		if(object != null){

			Application netApplication = (Application)object;
			Application application = ApplicationTranslator.getApplication(netApplication);

			String appName = application.getName();
			Application localApplication = operatorFindApplication(appName, errors);

			if(localApplication == null){
				errors.add("Application with name: " + appName + " does not exist");
				return -1;
			}

			if(!errors.isEmpty()){
				return -1;
			}

			Session session = null;
			Transaction tx = null;

			logger.info("Modyfing an application: " + appName);

			long localApplicationId = localApplication.getId();

			try{
				session = InitSessionFactory.getInstance().openSession();
				tx = session.beginTransaction();

				Application app = (Application)session.load(Application.class, localApplicationId);
				app.setType(application.getType());

				app.setBandwidth(application.getBandwidth());
				app.setJitter(application.getJitter());
				app.setLoss(application.getLoss());
				app.setDelay(application.getDelay());

				app.setCondBandwidth(application.getCondBandwidth());
				app.setCondJitter(application.getCondJitter());
				app.setCondLoss(application.getCondLoss());
				app.setCondDelay(application.getCondDelay());

				session.save(app);

				tx.commit();
				session.close();

				logger.info("Updating process ends with success. Generated locationId: " + app.getId());

			}catch(Exception e){
				saveException("Exception during modyfing application : " + application.getName(), e, errors);
				closeSession(tx, session);
			}

			return localApplicationId;
		}

		return -1;
	}


	private itti.com.pl.eda.tactics.operator.Network operatorLoadNetwork(Map<OperatorParameter, String> params, List<String> errors){

		logger.info("Selecting network");

		String networkName = params.get(OperatorParameter.Name);

		Session session = null;
		Network network = null;

		try{
			session = InitSessionFactory.getInstance().openSession();

			network = (Network)session.createCriteria(Network.class).add(Restrictions.eq("name", networkName)).uniqueResult();

			session.close();

			if(network != null){
				logger.info("Network loaded: " + network.toString());
			}

		}catch(Exception e){
			saveException("Exception during loading network", e, errors);
			closeSession(null, session);
		}
		return NetworkTranslator.getNetwork(network);
	}


	private Application operatorLoadApplication(Map<OperatorParameter, String> params, List<String> errors){

		logger.info("Selecting application");

		String applicationName = params.get(OperatorParameter.Name);

		Session session = null;
		Application application = null;

		try{
			session = InitSessionFactory.getInstance().openSession();

			application = (Application)session.createCriteria(Application.class).add(Restrictions.eq("name", applicationName)).uniqueResult();

			session.close();

			if(application != null){
				logger.info("Application loaded: " + application.toString());
			}

		}catch(Exception e){
			saveException("Exception during loading application", e, errors);
			closeSession(null, session);
		}
		return ApplicationTranslator.getApplication(application);
	}

	private long operatorAddApplication(Object object, List<String> errors) {

		if(object != null){

			Application applicationNet = (Application)object;
			Application application = ApplicationTranslator.getApplication(applicationNet);

			String appName = application.getName();
			Application localApp = operatorFindApplication(appName, errors);

			if(localApp != null){
				errors.add("Application with name: " + appName + " already exists");
				return -1;
			}

			if(!errors.isEmpty()){
				return -1;
			}

			Session session = null;
			Transaction tx = null;

			logger.info("Adding a new application: " + appName);

			try{
				session = InitSessionFactory.getInstance().openSession();
				tx = session.beginTransaction();

				session.save(application);

				tx.commit();
				session.close();

				logger.info("Adding process ends with success. Generated applicationId: " + application.getId());

			}catch(Exception e){
				saveException("Exception during adding new application : " + application.getName(), e, errors);
				closeSession(tx, session);
			}

			return application.getId();
		}

		return -1;
	}


	@SuppressWarnings("unchecked")
	private List<Application> operatorLoadApplications(Collection<String> errors) {

		Session session = null;
		List<Application> applications = null;

		logger.info("Loading applications");
		String query = "from Application";

		try{
			session = InitSessionFactory.getInstance().openSession();
			applications = session.createQuery(query).list();

			session.close();

		}catch(Exception e){
			saveException("Exception during selecting applications", e, errors);
			closeSession(null, session);
		}

		return applications;
	}


	@SuppressWarnings("unchecked")
	private List<PolicyQoSLevel> operatorLoadPolicyQoSLevels(Collection<String> errors) {

		Session session = null;
		List<PolicyQoSLevel> levels = null;

		logger.info("Loading qos levels");
		String query = "from PolicyQoSLevel";

		try{
			session = InitSessionFactory.getInstance().openSession();
			levels = session.createQuery(query).list();

			session.close();

		}catch(Exception e){
			saveException("Exception during selecting qos levels", e, errors);
			closeSession(null, session);
		}

		return levels;
	}


	@SuppressWarnings("unchecked")
	private boolean operatorDeleteNetwork(Map<OperatorParameter, String> argumentsMap, List<String> errors) {

		String networkName = argumentsMap.get(OperatorParameter.Name);

		Session session = null;
		Transaction tx = null;
		long networkId = -1;

		logger.info("Deleting network: " + networkName);

		try{
			session = InitSessionFactory.getInstance().openSession();
			tx = session.beginTransaction();

			Network network = (Network)session.createCriteria(Network.class).add(Restrictions.eq("name", networkName)).uniqueResult();
			if(network != null){
				networkId = network.getId();

				String query = "select location_name from locations where network_id =" + networkId;
				List<String> locations = (List<String>)session.createSQLQuery(query).list();
				if(locations == null || locations.isEmpty()){
					session.delete(network);
				}else{
					for (String string : locations) {
						errors.add("Network '" + networkName + "' is used by location '" + string + "'");
					}
				}
			}

			tx.commit();
			session.close();

			logger.info("Deleting process status: " + errors.isEmpty() + ". Deleted network with id: " + networkId);

		}catch(Exception e){
			saveException("Exception during deleting location : " + networkName, e, errors);
			closeSession(tx, session);
		}
		return errors.isEmpty();
	}

	private long operatorAddQoSPolicy(Object object, List<String> errors) {

		if(object != null){

			PolicyQoSLevel policy = (PolicyQoSLevel)object;
			itti.com.pl.eda.tactics.data.model.PolicyQoSLevel policyDb = PolicyTranslator.getPolicyQoSLevel(policy);

			String policyName = policyDb.getName();
			PolicyQoSLevel localPolicy = operatorFindQoSPolicy(policyName, errors);

			if(localPolicy != null){
				errors.add("QoS policy with name: " + policyName + " already exists");
				return -1;
			}

			if(!errors.isEmpty()){
				return -1;
			}

			Session session = null;
			Transaction tx = null;

			logger.info("Adding a new policy qos: " + policyName);

			try{
				session = InitSessionFactory.getInstance().openSession();
				tx = session.beginTransaction();

				session.save(policyDb);

				tx.commit();
				session.close();

				logger.info("Adding process ends with success. Generated policyQoSId: " + policyDb.getId());

			}catch(Exception e){
				saveException("Exception during adding new policy qos: " + policyDb.getName(), e, errors);
				closeSession(tx, session);
			}

			return policyDb.getId();
		}

		return -1;
	}


	private boolean operatorDeleteQoSPolicy(Map<OperatorParameter, String> argumentsMap, List<String> errors) {

		String policyName = argumentsMap.get(OperatorParameter.Name);

		Session session = null;
		Transaction tx = null;

		logger.info("Deleting policy qos: " + policyName);

		try{
			session = InitSessionFactory.getInstance().openSession();
			tx = session.beginTransaction();

			long id = -1;
			PolicyQoSLevel policy = (PolicyQoSLevel)session.createCriteria(PolicyQoSLevel.class).add(Restrictions.eq("name", policyName)).uniqueResult();
			if(policy != null){
				id = policy.getId();
				session.delete(policy);
			}

			tx.commit();
			session.close();

			logger.info("Deleting process ends with success. Deleted policy qos with id: " + id);

		}catch(Exception e){
			saveException("Exception during deleting policy qos: " + policyName, e, errors);
			closeSession(tx, session);
		}
		return errors.isEmpty();
	}


	private PolicyQoSLevel operatorLoadQoSPolicy(Map<OperatorParameter, String> params, List<String> errors){

		logger.info("Selecting policy qos");

		String policyName = params.get(OperatorParameter.Name);

		Session session = null;
		itti.com.pl.eda.tactics.data.model.PolicyQoSLevel policy = null;

		try{
			session = InitSessionFactory.getInstance().openSession();

			policy = (itti.com.pl.eda.tactics.data.model.PolicyQoSLevel)session.createCriteria(PolicyQoSLevel.class).add(Restrictions.eq("name", policyName)).uniqueResult();

			session.close();

			if(policy != null){
				logger.info("Policy qos loaded: " + policy.toString());
			}

		}catch(Exception e){
			saveException("Exception during loading policy", e, errors);
			closeSession(null, session);
		}
		return PolicyTranslator.getPolicyQoSLevel(policy);
	}


	private long operatorUpdateQoSPolicy(Object object, List<String> errors) {

		if(object != null){

			PolicyQoSLevel netPolicy = (PolicyQoSLevel)object;
			itti.com.pl.eda.tactics.data.model.PolicyQoSLevel policy = PolicyTranslator.getPolicyQoSLevel(netPolicy);

			String policyName = policy.getName();
			PolicyQoSLevel localPolicy = operatorFindQoSPolicy(policyName, errors);

			if(localPolicy == null){
				errors.add("Policy qos with name: " + policyName + " does not exist");
				return -1;
			}

			if(!errors.isEmpty()){
				return -1;
			}

			Session session = null;
			Transaction tx = null;

			logger.info("Modyfing an policy qos: " + policyName);

			long localPolicyId = localPolicy.getId();

			try{
				session = InitSessionFactory.getInstance().openSession();
				tx = session.beginTransaction();

				PolicyQoSLevel plcy = (PolicyQoSLevel)session.load(PolicyQoSLevel.class, localPolicyId);

				plcy.setBandwidth(policy.getBandwidth());
				plcy.setJitter(policy.getJitter());
				plcy.setLoss(policy.getLoss());
				plcy.setDelay(policy.getDelay());

				session.save(plcy);

				tx.commit();
				session.close();

				logger.info("Updating process ends with success. Generated policyQoSId: " + plcy.getId());

			}catch(Exception e){
				saveException("Exception during modyfing policy qos : " + policy.getName(), e, errors);
				closeSession(tx, session);
			}

			return localPolicyId;
		}

		return -1;
	}




	private void saveException(String msg, Exception error, Collection<String> errors) {

		error.printStackTrace();
		errors.add(msg);
		errors.add(error.toString());
		logger.warn(error.toString());
	}


	private void closeSession(Transaction tx, Session session){

		try{
			if(tx != null){
				tx.rollback();
			}
			if(session != null){
				session.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	/**
	 * returns last added policy name from database for specified ontology
	 * @param ontologyId id of the ontology
	 * @param errors possible errors
	 * @return name of the latest added policy
	 */
	@SuppressWarnings("unchecked")
	public long getMaxPolicyIdForOntology(long ontologyId, List<String> errors) {

		logger.info("Selecting policies related to ontology: " + ontologyId);

		String sqlQuery = "SELECT max(id) FROM policies_hl WHERE ontology_id = :ontId ";

		long result = -1;

		Session session = null;

		try{
			session = InitSessionFactory.getInstance().openSession();

			Query query = session.createSQLQuery(sqlQuery);

			query.setLong("ontId", ontologyId);
			List<Object> retList = query.list();
			if(retList == null || retList.isEmpty() || retList.get(0) == null){
				result = 0;
			}else{
				String res = retList.get(0).toString();
				result = Long.parseLong(res);
			}

			session.close();

			logger.info("Max policy selecting process ends with no errors");

			logger.info("Maximum policyId for ontology with id: " + ontologyId + " is equal: " + result);

		}catch(Exception e){
			saveException("Exception during selecting policy names with ontID" + ontologyId, e, errors);
			closeSession(null, session);
		}

		return result;
	}
}
