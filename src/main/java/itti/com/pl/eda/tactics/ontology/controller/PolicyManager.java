package itti.com.pl.eda.tactics.ontology.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import itti.com.pl.eda.tactics.data.model.Location;
import itti.com.pl.eda.tactics.data.model.Network;
import itti.com.pl.eda.tactics.data.model.PolicyHighLevel;
import itti.com.pl.eda.tactics.data.model.PolicyIntermediateLevel;
import itti.com.pl.eda.tactics.data.model.PolicyQoSLevel;
import itti.com.pl.eda.tactics.data.model.QoSLevel;
import itti.com.pl.eda.tactics.data.model.QosParameter;
import itti.com.pl.eda.tactics.data.model.policy.PolicyILCondition;
import itti.com.pl.eda.tactics.exception.PolicyManagerException;
import itti.com.pl.eda.tactics.exception.PolicyManagerException.PolicyManagerExceptionType;
import itti.com.pl.eda.tactics.utils.PoldConstants;
import itti.com.pl.eda.tactics.utils.PoldSettings;
import itti.com.pl.eda.tactics.utils.StringUtils;
import itti.com.pl.eda.tactics.exception.RefinementCalculatorException;
import itti.com.pl.eda.tactics.network.message.OperatorCommands;
import itti.com.pl.eda.tactics.ontology.swrl.SwrlPolicyType;
import itti.com.pl.eda.tactics.ontology.swrl.SwrlWrapper;
import itti.com.pl.eda.tactics.policy.LogicalOperator;
import itti.com.pl.eda.tactics.policy.PolicyTriple;
import itti.com.pl.eda.tactics.policy.PolicyTripleOperator;
import itti.com.pl.eda.tactics.policy.PolicyTripleVariable;
import itti.com.pl.eda.tactics.policy.impl.PolicyTripleImpl;

/**
 * class responsible for all operations and manipulations on ontology models
 * 
 * @author marcin
 * 
 */
public class PolicyManager {

	Logger logger = Logger.getLogger(PolicyManager.class);

	private static final String dummyClassName = "sample_class";
	private long dummyClassPosition = -1;

	// private JenaOWLModel modelQos = null;
	// private JenaOWLModel modelPolicy = null;
	private JenaOWLModel modelFinal = null;

	private RefinementController refinementController = null;

	public PolicyManager() {

		logger.info("Policy Manager initialization");

		logger.info("Refinement controller initialization");
		refinementController = new RefinementController();
		refinementController.init();

	}

	/**
	 * initializes weight calculator weigh calculator is a part of policy
	 * refinement process
	 * 
	 * @param weights
	 *            list of QoS classes and their weights retrieved from POLD
	 *            database
	 * @return true, if init process ends with success, false otherwise
	 * @throws RefinementCalculatorException
	 */
	public boolean initWeightsCalculator(List<Object[]> weights)
			throws RefinementCalculatorException {
		return refinementController.constructWeightsCalculator(weights);
	}

	/**
	 * adds reference to the ontology model
	 * 
	 * @param ontologyType
	 *            type of the ontology
	 * @param ontologyModel
	 *            in-memory owl ontology model
	 * @throws PolicyManagerException
	 */
	public void setOntology(OntologyType ontologyType,
			JenaOWLModel ontologyModel) throws PolicyManagerException {

		logger.debug("Adding new ontology to policy manager: "
				+ (ontologyType != null ? ontologyType.name() : "NULL "));

		if (ontologyType != null && ontologyModel != null) {

			logger.debug("Adding new ontology: " + ontologyType.name());

			switch (ontologyType) {
			case PolicyOntology:
				// modelPolicy = ontologyModel;
				break;

			case QoSOntology:
				// modelQos = ontologyModel;
				break;
			case FinalOntology:
				modelFinal = ontologyModel;
				break;
			}

		} else if (ontologyType == null) {
			throw new PolicyManagerException(
					PolicyManagerExceptionType.OntologyIsNull,
					"Ontology model name is null");
		} else {
			throw new PolicyManagerException(
					PolicyManagerExceptionType.OntologyIsNull,
					"Ontology is null for model " + ontologyType.name());
		}
	}

	/**
	 * merges ontology model with list of policies
	 * 
	 * @param policiesList
	 *            list of policies
	 */
	public void updatePoliciesWithOntlology(List<PolicyHighLevel> policiesList) {

		logger.info("Updating policies with ontology");

		if (policiesList != null && !policiesList.isEmpty()) {

			for (PolicyHighLevel policyHighLevel : policiesList) {
				// addPolicyToOWLModel(policyHighLevel, modelPolicy);
				if (modelFinal != null) {
					addPolicyToOWLModel(policyHighLevel, modelFinal);
				}
			}

		} else {
			logger.debug("There is nothing to update");
		}
	}

	public boolean addPolicyToOWLModel(PolicyHighLevel phl, JenaOWLModel model) {

		try {
			String policyName = phl.getName();
			OWLIndividual policy = createSimpleInstance(model, "Policy",
					policyName, null);

			if (phl.getActions() != null) {
				for (PolicyTriple singleAction : phl.getActions().getElements()) {
					String policyActionName = singleAction.getVariable() + '_'
							+ singleAction.getOperator() + '_'
							+ singleAction.getValue();

					// validation: are policy elements exist in owl ontology
					OWLIndividual indActOper = model
							.getOWLIndividual(singleAction.getOperator());
					if (indActOper == null) {
						indActOper = createSimpleInstance(model,
								PoldConstants.ActionOperator, singleAction
										.getOperator(), null);
					}
					OWLIndividual indActVal = model
							.getOWLIndividual(singleAction.getValue());
					if (indActVal == null) {
						indActVal = createSimpleInstance(model,
								PoldConstants.ActionValue, singleAction
										.getValue(), null);
					}
					OWLIndividual indActVar = model
							.getOWLIndividual(singleAction.getVariable());
					if (indActVar == null) {
						indActVar = createSimpleInstance(model,
								PoldConstants.ActionVariable, singleAction
										.getVariable(), null);
					}
					OWLIndividual policyAction = createSimpleInstance(model,
							"NetQoSSimplePolicyAction", policyActionName, null);

					RDFProperty propAct = model
							.getRDFProperty("Policy-hasPolicyAction");
					addProperty(policy, propAct, policyAction);

					RDFProperty policyOperatorAction = model
							.getRDFProperty(PoldConstants.PropertyOperatorAction);
					addProperty(policyAction, policyOperatorAction, indActOper);

					RDFProperty policyVariableAction = model
							.getRDFProperty(PoldConstants.PropertyVariableAction);
					addProperty(policyAction, policyVariableAction, indActVar);

					RDFProperty policyValueAction = model
							.getRDFProperty(PoldConstants.PropertyValueAction);
					addProperty(policyAction, policyValueAction, indActVal);
				}
			}

			if (phl.getConditions() != null) {
				for (PolicyTriple singleCondition : phl.getConditions()
						.getElements()) {
					String policyConditionName = singleCondition.getVariable()
							+ '_' + singleCondition.getOperator() + '_'
							+ singleCondition.getValue();

					OWLIndividual indConOper = model
							.getOWLIndividual(singleCondition.getOperator());
					if (indConOper == null) {
						indConOper = createSimpleInstance(model,
								PoldConstants.ActionOperator, singleCondition
										.getOperator(), null);
					}
					OWLIndividual indConVal = model
							.getOWLIndividual(singleCondition.getValue());
					if (indConVal == null) {
						indConVal = createSimpleInstance(model,
								PoldConstants.ActionValue, singleCondition
										.getValue(), null);
					}
					OWLIndividual indConVar = model
							.getOWLIndividual(singleCondition.getVariable());
					if (indConVar == null) {
						indConVar = createSimpleInstance(model,
								PoldConstants.ActionVariable, singleCondition
										.getVariable(), null);
					}

					OWLIndividual policyCondition = createSimpleInstance(model,
							"NetQoSSimplePolicyCondition", policyConditionName,
							null);

					RDFProperty propCon = model
							.getRDFProperty("Policy-hasPolicyCondition");
					addProperty(policy, propCon, policyCondition);

					RDFProperty policyOperatorCondition = model
							.getRDFProperty(PoldConstants.PropertyOperatorCondition);
					addProperty(policyCondition, policyOperatorCondition,
							indConOper);

					RDFProperty policyVariableCondition = model
							.getRDFProperty(PoldConstants.PropertyVariableCondition);
					addProperty(policyCondition, policyVariableCondition,
							indConVar);

					RDFProperty policyValueCondition = model
							.getRDFProperty(PoldConstants.PropertyValueCondition);
					addProperty(policyCondition, policyValueCondition,
							indConVal);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	private static void addProperty(OWLIndividual subject,
			RDFProperty property, Object value) {
		if (subject != null && property != null
				&& subject.getPropertyValue(property) == null) {
			subject.addPropertyValue(property, value);
		}
	}

	@SuppressWarnings("unchecked")
	private static void addMultiProperty(OWLIndividual subject,
			RDFProperty property, Object value) {
		if (subject != null && property != null) {
			Collection<Object> currentOntValue = subject
					.getPropertyValues(property);
			if (currentOntValue == null || currentOntValue.isEmpty()) {
				subject.addPropertyValue(property, value);
			} else {
				boolean exists = false;
				for (Object resource : currentOntValue) {
					if (resource.equals(value)) {
						exists = true;
						break;
					}
				}
				if (!exists) {
					subject.addPropertyValue(property, value);
				}
			}
		}
	}

	/**
	 * creates simple instance in owl ontology model
	 * 
	 * @param model
	 *            reference to the ontology model
	 * @param className
	 *            name of the ontology class
	 * @param instanceName
	 *            name of the instance
	 * @return reference to the newly created instance
	 */
	static OWLIndividual createSimpleInstance(JenaOWLModel model,
			String className, String instanceName,
			Map<String, List<String>> properties) {

		OWLIndividual individual = null;

		if (model.getOWLIndividual(instanceName) != null) {
			individual = model.getOWLIndividual(instanceName);
		} else {
			OWLNamedClass parentClass = getClass(model, className);
			individual = parentClass.createOWLIndividual(instanceName);
		}

		if (individual != null) {

			String prefix = individual.getRDFType().getNamespacePrefix();
			if (properties != null && !properties.isEmpty()) {
				for (String key : properties.keySet()) {
					RDFProperty property = getProperty(model, key, prefix);
					if (property == null) {
						continue;
					}

					List<String> values = properties.get(key);
					for (String value : values) {
						OWLIndividual valueInd = getIndividual(model, value);
						if (valueInd != null) {
							addMultiProperty(individual, property, valueInd);
						} else if (StringUtils.isInt(value)) {
							int valueInt = StringUtils.getIntValue(value);
							addMultiProperty(individual, property, new Float(
									valueInt));
						} else {
							addMultiProperty(individual, property, value);
						}
					}
				}
			}
			return individual;
		}
		return null;
	}

	public boolean createOwlClass(String className) {

		// JenaOWLModel model = modelQos;
		JenaOWLModel model = modelFinal;

		if (model.getOWLNamedClass(className) == null) {
			OWLNamedClass individual = model.createOWLNamedClass(className);
			return individual != null;
		}
		return false;
	}

	/**
	 * matches policy with the list of other policies and looks for possible
	 * conflicts
	 * 
	 * @param policy
	 *            policy which should be checked
	 * @param policies
	 *            list of policies used for checking
	 * @param errList
	 *            possible errors
	 */
	public void matchPolicies(PolicyHighLevel policy,
			List<PolicyHighLevel> policies, List<String> errList) {

		if (policies != null) {
			for (PolicyHighLevel currentPolicy : policies) {
				lookForConflicts(currentPolicy, policy, errList);
			}
		}
	}

	private boolean lookForConflicts(PolicyHighLevel currPolicy,
			PolicyHighLevel newPolicy, List<String> errors) {

		if (StringUtils.isNullOrEmpty(newPolicy.getName())) {
			errors.add("Policy name is empty");
		}
		// compare policies names
		if (StringUtils.compareStrings(currPolicy.getName(), newPolicy
				.getName())) {
			errors.add("Policy with name: " + currPolicy.getName()
					+ " already exists");
		}

		if (currPolicy.getConditions() != null
				&& currPolicy.getTimeConditions() != null) {
			if (currPolicy.getConditions().equals(newPolicy.getConditions())
					&& currPolicy.getTimeConditions().equals(
							newPolicy.getTimeConditions())) {
				errors.add("Conditions conflict with policy with ID: "
						+ currPolicy.getId() + ", name: "
						+ currPolicy.getName()
						+ ". Both policies have the same conditions");
			}
		}
		return errors.isEmpty();
	}

	/**
	 * returns swrl-defined policy type from the ontology model
	 * 
	 * @param policyName
	 *            name of the policy
	 * @return type of the policy
	 */
	public String getPolicyTypeFromModel(String policyName) {

		OWLIndividual policy = null;

		// if(modelPolicy != null){
		if (modelFinal != null) {
			// policy = modelPolicy.getOWLIndividual(policyName);
			policy = modelFinal.getOWLIndividual(policyName);
		}
		if (policy != null) {

			// RDFProperty prop =
			// modelPolicy.getRDFProperty("Policy-PolicyType");
			RDFProperty prop = modelFinal.getRDFProperty("Policy-PolicyType");
			if (prop != null) {
				String val = (String) policy.getPropertyValue(prop);
				return val;
			}

			return "";
		}
		return null;
	}

	/**
	 * refines high-level policy refinement means process of translation of the
	 * high-level policy into list of qos parameters stored in
	 * intermediate-level policies
	 * 
	 * @param policy
	 *            high-level policy which should be refined
	 * @param errors
	 *            possible errors
	 * @return list of intermediate-level policies (result of the refinement)
	 */
	public List<PolicyIntermediateLevel> refinePolicy(PolicyHighLevel policy,
			List<String> errors) {

		switch (PoldSettings.RefinementMethod) {
		case JavaBased:
			return refinePolicyJava(policy, errors);
		case SwrlBased:
			return refinePolicySwrl(policy, errors);
		}
		return null;
	}

	@SuppressWarnings( { "unchecked" })
	private List<PolicyIntermediateLevel> refinePolicySwrl(
			PolicyHighLevel policy, List<String> errors) {

		String policyName = policy.getName();
		String instanceName = "Refine_" + policyName;
		String policyClass = "Policy";
		OWLNamedClass parentClass = getClass(modelFinal, policyClass);
		OWLIndividual instanceRefine = null;

		if (parentClass != null) {

			Map<String, List<String>> props = new HashMap<String, List<String>>();

			List<String> condProps = new ArrayList<String>();
			if (policy.getConditions() != null) {
				for (PolicyTriple triple : policy.getConditions().getElements()) {

					String propertyName = createPolicyTriple(triple, true);
					condProps.add(propertyName);
				}
			}
			props.put("Policy-hasPolicyCondition", condProps);

			List<String> actionProps = new ArrayList<String>();
			if (policy.getActions() != null) {

				QoSLevel level = null;
				boolean levelIsNew = false;

				for (PolicyTriple triple : policy.getActions().getElements()) {

					if(
							triple.getVariableEnum() == PolicyTripleVariable.Bandwidth || 
							triple.getVariableEnum() == PolicyTripleVariable.Delay ||
							triple.getVariableEnum() == PolicyTripleVariable.Jitter ||
							triple.getVariableEnum() == PolicyTripleVariable.Loss
					){
						if(level == null){
							level = new QoSLevel();
							levelIsNew = true;
						}
						QosParameter param = null;
						try{
							param = new QosParameter(triple.getVariableEnum());
							param.setValue(StringUtils.getIntValue(triple.getValue()));
							level.addQosParameter(param);

						}catch (Exception e) {}

					}else if (triple.getVariableEnum() == PolicyTripleVariable.Qos_level){
						level = getQoSLevel(triple.getValue());
						String propertyName = createPolicyTriple(triple, true);
						actionProps.add(propertyName);
					}else{
						String propertyName = createPolicyTriple(triple, false);
						actionProps.add(propertyName);						
					}
				}
				if(level != null && levelIsNew){
					PolicyTriple qosTriple = new PolicyTripleImpl();
					String propertyName = createQoSPolicyTriple(policyName, level, false);

					PolicyQoSLevel policyQoS = new PolicyQoSLevel();
					policyQoS.setName(propertyName);
					policyQoS.setBandwidth(level.getBandwidth());
					policyQoS.setJitter(level.getJitter());
					policyQoS.setDelay(level.getDelay());
					policyQoS.setLoss(level.getLoss());

					PoldController.hibernateWrapper.processOperatorRequest(
							OperatorCommands.AddQoSPolicy, 
							null, 
							errors, 
							policyQoS);

					qosTriple.addElement(
							new PolicyTripleImpl(PolicyTripleVariable.Qos_level, 
									PolicyTripleOperator.Set, propertyName));
					actionProps.add(propertyName);

				}
			}
			props.put("Policy-hasPolicyAction", actionProps);

			createSimpleInstance(modelFinal, policyClass, policyName, props);

			String refineClass = "Policy_refinement";
			Map<String, List<String>> refineProps = new HashMap<String, List<String>>();
			List<String> refineList = new ArrayList<String>();
			refineList.add(policyName);
			refineProps.put("Refinement-isRelatedToPolicy", refineList);
			instanceRefine = createSimpleInstance(modelFinal, refineClass,
					instanceName, refineProps);
		}

		List<PolicyIntermediateLevel> pilList = new ArrayList<PolicyIntermediateLevel>();

		SwrlWrapper swrlWrapper = new SwrlWrapper();
		if (swrlWrapper.runSwrlRules(modelFinal, errors)) {

			String netProperty = "Available_Network";
			String weigthProperty = "Netrwork_has_weight";
			RDFProperty propertyNet = getProperty(modelFinal, netProperty, null);
			RDFProperty propertyWeigth = getProperty(modelFinal,
					weigthProperty, null);

			Collection<OWLIndividual> networksRefine = instanceRefine
					.getPropertyValues(propertyNet);

			if (networksRefine != null && !networksRefine.isEmpty()) {

				Map<String, Integer> weights = new HashMap<String, Integer>();

				for (OWLIndividual networkInstance : networksRefine) {

					String networkName = networkInstance.getName();

					Integer totalWeight = 0;
					Collection<Object> weigthParam = networkInstance
							.getPropertyValues(propertyWeigth);
					for (Object singleWeight : weigthParam) {

						if (singleWeight != null) {
							int weight = StringUtils.getIntValue(singleWeight
									.toString());
							totalWeight += weight;
							if (weight < 0 && totalWeight >= 0) {
								totalWeight = -1;
							}
						}
						networkInstance.removePropertyValue(propertyWeigth,
								singleWeight);
					}

					if (totalWeight > 0) {

						PolicyIntermediateLevel pil = new PolicyIntermediateLevel();

						PolicyTriple ps = new PolicyILCondition(
								PolicyTripleVariable.Network,
								PolicyTripleOperator.Equals, networkName);
						PolicyTriple conditions = new PolicyILCondition(
								LogicalOperator.And);
						conditions.addElement(ps);
						pil.setConditions(conditions);

						pil.setParent(policy);
						pil.setTimeConditions(policy.getTimeConditions());
						pil.setAuthor(policy.getAuthor());

						RDFProperty applicationProperty = getProperty(
								modelFinal, "Selected_Application", null);
						if (applicationProperty != null) {
							OWLIndividual applicationOwl = (OWLIndividual) instanceRefine
									.getPropertyValue(applicationProperty);
							pil.setApplication(applicationOwl.getName());
						}

						RDFProperty ipProperty = getProperty(modelFinal,
								"Network-hasIPAddressRange", null);
						if (ipProperty != null) {
							String ipAddressPolicy = networkInstance
									.getPropertyValue(ipProperty).toString();
							pil.setIpAddress(ipAddressPolicy);
						}

						OWLIndividual cosInstance = (OWLIndividual) networkInstance
								.getPropertyValue(getProperty(modelFinal,
										"Network-hasCos_Support", null));

						if (cosInstance != null) {

							QoSLevel qosLevel = getQoSLevel(cosInstance);
							pil.setQosLevel(qosLevel);
						}

						pil.setWeight(totalWeight);
						pilList.add(pil);

						weights.put(networkName, totalWeight);
					}
				}
			}
		}
		return pilList;
	}

	private QoSLevel getQoSLevel(String levelName) {

		if(modelFinal != null){
			OWLIndividual level = getIndividual(modelFinal, levelName);
			if(level != null){

				RDFProperty levelProperty = getProperty(modelFinal, "QoSPolicyLevel-isRelatedtoRealQoSLevel", null);
				if(levelProperty != null && level.getPropertyValue(levelProperty) != null){
					OWLIndividual qosInstance = (OWLIndividual)level.getPropertyValue(levelProperty);
					if(qosInstance != null){
						return getQoSLevel(qosInstance);
					}
				}
			}
		}
		return null;
	}


	@SuppressWarnings("unchecked")
	private QoSLevel getQoSLevel(OWLIndividual qosInstance) {

		QoSLevel qosLevel = new QoSLevel();

		Collection<OWLIndividual> qosParams = qosInstance
				.getPropertyValues(getProperty(modelFinal,
						"QoSLevel-hasQoSParameter", null));

		for (OWLIndividual individual : qosParams) {
			String parentClassName = individual
					.getRDFType().getName();
			String prefix = StringUtils
					.getPrefix(parentClassName);
			RDFProperty propertyValue = getProperty(
					modelFinal, "QoSParameter-hasValue",
					prefix);
			Object valueObj = individual
					.getPropertyValue(propertyValue);
			int value = StringUtils
					.getIntValue(valueObj != null ? valueObj
							.toString()
							: null);
			parentClassName = parentClassName
					.substring(parentClassName.indexOf('_') + 1);
			if (parentClassName == "Throughput") {
				parentClassName = "Bandwidth";
			}
			PolicyTripleVariable qosVariable = PolicyTripleVariable
					.getValue(parentClassName);
			if (qosVariable != null) {
				switch (qosVariable) {
				case Bandwidth:
					qosLevel.setBandwidth(value);
					break;
				case Jitter:
					qosLevel.setJitter(value);
					break;
				case Delay:
					qosLevel.setDelay(value);
					break;
				case Loss:
					qosLevel.setLoss(value);
					break;
				default:
					break;
				}
			}
		}
		return qosLevel;
	}


	private List<PolicyIntermediateLevel> refinePolicyJava(
			PolicyHighLevel policy, List<String> errors) {
		List<PolicyIntermediateLevel> pilList = new ArrayList<PolicyIntermediateLevel>();

		String application = getConditionValue(policy,
				PolicyTripleVariable.Application);
		if (application == null) {
			errors
					.add("Application parameter has not been set! Refinement failed");
			return pilList;
		}

		String appType = null;
		if (application != null) {
			appType = modelFinal.getInstance(application + "_type") != null ? modelFinal
					.getInstance(application + "_type").getDirectType()
					.getName()
					: null;
		}

		if (appType == null) {
			errors.add("Application parameter's value is invalid: "
					+ application);
			return pilList;
		} else {
			appType = StringUtils.removePrefix(appType);
		}
		// zmapowianie lokalizacji Policy na QoS
		String userLocalization = getConditionValue(policy,
				PolicyTripleVariable.Localization);
		String ipAddress = getConditionValue(policy,
				PolicyTripleVariable.IpAddress);

		if (userLocalization == null && ipAddress == null) {
			errors
					.add("Localization or IpAddress parameters have not been set! Refinement failed");
			return pilList;
		}

		String userName = policy.getAuthor().getName();

		String bandwidth = getActionValue(policy,
				PolicyTripleVariable.Bandwidth);
		String jitter = getActionValue(policy, PolicyTripleVariable.Jitter);
		String delay = getActionValue(policy, PolicyTripleVariable.Delay);
		String loss = getActionValue(policy, PolicyTripleVariable.Loss);

		Map<String, Network> availableNetworks = findNetworks(policy
				.getAuthor().getLocations(), userLocalization, ipAddress);

		if (bandwidth == null && jitter == null && delay == null) {

			try {
				// Map<String, Collection<QosParameter>> nets =
				// SPARQLWrapper.getUserNetworks(modelQos, userName);
				Map<String, Collection<QosParameter>> nets = SPARQLWrapper
						.getUserNetworks(modelFinal, userName);

				for (String netName : nets.keySet()) {

					String ipAddressPolicy = null;

					if (availableNetworks == null) {
						continue;
					} else {

						for (String policyIp : availableNetworks.keySet()) {
							Network network = availableNetworks.get(policyIp);
							if (network.getName().equals(netName)) {
								ipAddressPolicy = policyIp;
							}
						}
					}
					if (ipAddressPolicy == null) {
						continue;
					}

					Collection<QosParameter> attrs = nets.get(netName);
					int sum = refinementController.calculateWeight(appType,
							attrs);

					PolicyIntermediateLevel pil = new PolicyIntermediateLevel();

					PolicyTriple ps = new PolicyILCondition(
							PolicyTripleVariable.Network,
							PolicyTripleOperator.Equals, netName);
					PolicyTriple conditions = new PolicyILCondition(
							LogicalOperator.And);
					conditions.addElement(ps);
					pil.setConditions(conditions);

					pil.setParent(policy);
					pil.setTimeConditions(policy.getTimeConditions());
					pil.setAuthor(policy.getAuthor());
					pil.setApplication(application);
					pil.setIpAddress(ipAddressPolicy);

					QoSLevel qosLevel = new QoSLevel();
					for (QosParameter qosProperty : nets.get(netName)) {
						qosLevel.addQosParameter(qosProperty);
					}
					pil.setQosLevel(qosLevel);
					pil.setWeight(sum);
					pil.setTimeConditions(policy.getTimeConditions());
					pilList.add(pil);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			PolicyIntermediateLevel pil = new PolicyIntermediateLevel();

			QoSLevel qosLevel = new QoSLevel();
			qosLevel.setBandwidth(StringUtils.getIntValue(bandwidth));
			qosLevel.setDelay(StringUtils.getIntValue(delay));
			qosLevel.setJitter(StringUtils.getIntValue(jitter));
			qosLevel.setLoss(StringUtils.getIntValue(loss));

			PolicyTripleOperator bandwidthOperator = getActionOperator(policy,
					PolicyTripleVariable.Bandwidth);
			PolicyTripleOperator jitterOperator = getActionOperator(policy,
					PolicyTripleVariable.Jitter);
			PolicyTripleOperator delayOperator = getActionOperator(policy,
					PolicyTripleVariable.Delay);
			PolicyTripleOperator lossOperator = getActionOperator(policy,
					PolicyTripleVariable.Loss);

			String netName = null;

			if (availableNetworks != null && ipAddress == null) {

				for (String policyIp : availableNetworks.keySet()) {
					Network network = availableNetworks.get(policyIp);
					if (network.matchesCriteria(qosLevel, bandwidthOperator,
							delayOperator, jitterOperator, lossOperator)) {
						ipAddress = policyIp;
						netName = network.getName();
						break;
					}
				}
			}

			// PolicyTriple ps1 = new
			// PolicyILCondition(PolicyTripleVariable.IpAddress,
			// PolicyTripleOperator.Equals, ipAddress);
			// PolicyTriple ps2 = new
			// PolicyILCondition(PolicyTripleVariable.Application,
			// PolicyTripleOperator.Equals, application);
			// PolicyTriple conditions = new
			// PolicyILCondition(LogicalOperator.And);
			// conditions.addElement(ps1);
			// conditions.addElement(ps2);
			// pil.setConditions(conditions);
			PolicyTriple ps = new PolicyILCondition(
					PolicyTripleVariable.Network, PolicyTripleOperator.Equals,
					netName);
			PolicyTriple conditions = new PolicyILCondition(LogicalOperator.And);
			conditions.addElement(ps);
			pil.setConditions(conditions);

			pil.setParent(policy);
			pil.setTimeConditions(policy.getTimeConditions());
			pil.setAuthor(policy.getAuthor());
			pil.setApplication(application);
			pil.setIpAddress(ipAddress);

			pil.setQosLevel(qosLevel);
			pil.setTimeConditions(policy.getTimeConditions());
			if (ipAddress != null) {
				pilList.add(pil);
			}

		}
		return pilList;
	}

	private static OWLNamedClass getClass(JenaOWLModel model, String className) {

		OWLNamedClass parentClass = model.getOWLNamedClass(className);
		if (parentClass != null) {
			return parentClass;
		} else {
			for (Cls ontoClass : model.getClses()) {
				if (ontoClass.getName().toLowerCase().endsWith(
						className.toLowerCase())) {
					if (ontoClass.getName().toLowerCase().endsWith(
							":" + className.toLowerCase())) {
						return model.getOWLNamedClass(ontoClass.getName());
					}
				}
			}
		}
		return null;
	}

	private static OWLIndividual getIndividual(JenaOWLModel model,
			String individual) {

		OWLIndividual valueInd = model.getOWLIndividual(individual);
		if (valueInd == null) {
			for (Object owlIndividual : model.getOWLIndividuals()) {
				OWLIndividual ind = (OWLIndividual) owlIndividual;
				if (ind.getName().toLowerCase().endsWith(
						individual.toLowerCase())) {
					if (ind.getName().toLowerCase().endsWith(
							":" + individual.toLowerCase())) {
						return ind;
					}
				}
			}
		}
		return valueInd;
	}

	@SuppressWarnings("unchecked")
	private static RDFProperty getProperty(JenaOWLModel model,
			String propertyName, String prefix) {

		RDFProperty property = model.getRDFProperty(propertyName);
		if (property != null) {
			return property;
		} else {
			Collection<RDFProperty> rdfProperties = model.getRDFProperties();
			for (RDFProperty prop : rdfProperties) {
				if (prop.getName().toLowerCase().endsWith(
						propertyName.toLowerCase())) {
					String propName = prop.getName().toLowerCase();
					if (prefix != null
							&& propName.equals(prefix + ":"
									+ propertyName.toLowerCase())) {
						return prop;
					} else if (prefix == null
							&& propName.endsWith(":"
									+ propertyName.toLowerCase())) {
						return prop;
					}
				}
			}
		}
		return null;
	}

	private Map<String, Network> findNetworks(Set<Location> locations,
			String userLocalization, String ipAddress) {

		if (locations != null) {
			for (Location location : locations) {
				if (userLocalization != null
						&& location.getLocationName().equals(userLocalization)) {
					return location.getNetworks();
				} else if (ipAddress != null) {
					for (String locIp : location.getNetworks().keySet()) {
						if (locIp.equals(ipAddress)) {
							Map<String, Network> map = new HashMap<String, Network>();
							map.put(locIp, location.getNetworks().get(locIp));
							return map;
						}
					}
				}
			}
		}
		return null;
	}

	private String createQoSPolicyTriple(String policyName, QoSLevel level, boolean isCondition) {

		String qosLevelClassName = "QoS_Level_PolicyActionVar";
		String qosLevelInstanceName = policyName + "_qosLevel";

		String qosProperty = "QoSLevel-hasQoSParameter";
		String qosValueProperty = "QoSParameter-hasValue";

		Map<String, List<String>> properties = new HashMap<String, List<String>>();
		properties.put(qosProperty, new ArrayList<String>());

		if(level.getBandwidth() > 0){

			String qosClassName = "Bandwidth_" + level.getBandwidth();
			Map<String, List<String>> props = new HashMap<String, List<String>>();
			props.put(qosValueProperty, new ArrayList<String>());
			props.get(qosValueProperty).add(level.getBandwidth() + "");
			createSimpleInstance(modelFinal, "QoSParameter_Throughput", qosClassName, props);

			properties.get(qosProperty).add(qosClassName);
		}

		if(level.getJitter() > 0){

			String qosClassName = "Jitter_" + level.getJitter();
			Map<String, List<String>> props = new HashMap<String, List<String>>();
			props.put(qosValueProperty, new ArrayList<String>());
			props.get(qosValueProperty).add(level.getJitter() + "");
			createSimpleInstance(modelFinal, "QoSParameter_Jitter", qosClassName, props);

			properties.get(qosProperty).add(qosClassName);
		}

		if(level.getDelay() > 0){

			String qosClassName = "Delay_" + level.getBandwidth();
			Map<String, List<String>> props = new HashMap<String, List<String>>();
			props.put(qosValueProperty, new ArrayList<String>());
			props.get(qosValueProperty).add(level.getDelay() + "");
			createSimpleInstance(modelFinal, "QoSParameter_Delay", qosClassName, props);

			properties.get(qosProperty).add(qosClassName);
		}

		if(level.getLoss() > 0){

			String qosClassName = "Loss_" + level.getBandwidth();
			Map<String, List<String>> props = new HashMap<String, List<String>>();
			props.put(qosValueProperty, new ArrayList<String>());
			props.get(qosValueProperty).add(level.getLoss() + "");
			createSimpleInstance(modelFinal, "QoSParameter_Loss", qosClassName, props);

			properties.get(qosProperty).add(qosClassName);
		}

		createSimpleInstance(modelFinal, qosLevelClassName, qosLevelInstanceName, null);

		return qosLevelInstanceName;
	}


	private String createPolicyTriple(PolicyTriple triple, boolean isCondition) {

		String variable = triple.getVariable();
		String operator = triple.getOperator();
		String value = triple.getValue();

		String property = "Condition";
		if (!isCondition) {
			property = "Action";
		}

		String tripleName = variable + '_' + operator + '_' + value;

		if (modelFinal.getInstance(tripleName) == null) {

			String className = "NetQoSSimplePolicy" + property;

			if (triple.getVariableEnum() == PolicyTripleVariable.Localization) {
				className = "Localization" + property;
			}

			String operatorProperty = "PolicyOperatorInSimplePolicy" + property;
			String variableProperty = "PolicyVariableInSimplePolicy" + property;
			String valueProperty = "PolicyValueInSimplePolicy" + property;

			Map<String, List<String>> properties = new HashMap<String, List<String>>();

			List<String> operList = new ArrayList<String>();
			operList.add(operator);
			properties.put(operatorProperty, operList);

			List<String> varList = new ArrayList<String>();
			varList.add(variable);
			properties.put(variableProperty, varList);

			List<String> valList = new ArrayList<String>();
			valList.add(value);
			properties.put(valueProperty, valList);

			createSimpleInstance(modelFinal, className, tripleName, properties);
		}
		return tripleName;
	}

	/**
	 * returns value of the specified condition variable
	 * 
	 * @param variable
	 *            searched variable
	 * @return value of the condition value which matches specified criteria
	 */
	public String getConditionValue(PolicyHighLevel phl,
			PolicyTripleVariable variable) {

		if (phl.getConditions() != null && variable != null) {
			Iterator<PolicyTriple> conditionsIterator = phl.getConditions()
					.getElements().iterator();
			while (conditionsIterator.hasNext()) {
				PolicyTriple section = conditionsIterator.next();
				if (section.getVariableEnum() == variable) {
					return section.getValue();
				}
			}
			return null;
		} else {
			return null;
		}
	}

	/**
	 * returns value of the specified condition variable
	 * 
	 * @param variable
	 *            searched variable
	 * @return value of the condition value which matches specified criteria
	 */
	public String getActionValue(PolicyHighLevel phl,
			PolicyTripleVariable variable) {

		if (phl.getActions() != null && variable != null) {
			Iterator<PolicyTriple> actionsIterator = phl.getActions()
					.getElements().iterator();
			while (actionsIterator.hasNext()) {
				PolicyTriple section = actionsIterator.next();
				if (section.getVariableEnum() == variable) {
					return section.getValue();
				}
			}
			return null;
		} else {
			return null;
		}
	}

	public PolicyTripleOperator getActionOperator(PolicyHighLevel phl,
			PolicyTripleVariable variable) {

		if (phl.getActions() != null && variable != null) {
			Iterator<PolicyTriple> actionsIterator = phl.getActions()
					.getElements().iterator();
			while (actionsIterator.hasNext()) {
				PolicyTriple section = actionsIterator.next();
				if (section.getVariableEnum() == variable) {
					return section.getOperatorEnum();
				}
			}
			return null;
		} else {
			return null;
		}
	}

	/**
	 * validates policy validation process checks (based on ontology model) if
	 * policy has no errors
	 * 
	 * @param highLevelPolicy
	 *            high-level policy which should be checked for errors
	 * @param errList
	 *            possible errors
	 * @return true, if policy is valid, false otherwise
	 */
	public boolean validatePolicy(PolicyHighLevel highLevelPolicy,
			Collection<String> errList) {

		if (highLevelPolicy == null) {
			errList.add("No policy was given to validate");
			return false;
		}

		if (highLevelPolicy.getName() == null) {
			errList.add("required parameter set to null: PolicyName");
		}

		Iterator<PolicyTriple> conditionIterator = highLevelPolicy
				.getConditions().getElements().iterator();
		while (conditionIterator.hasNext()) {
			PolicyTriple condition = conditionIterator.next();

			validatePolicyElement("Condition operator",
					condition.getOperator(), errList);
			validatePolicyElement("Condition variable",
					condition.getVariable(), errList);
			validatePolicyElement("Condition value", condition.getValue(),
					errList);
		}

		Iterator<PolicyTriple> actionIterator = highLevelPolicy.getActions()
				.getElements().iterator();
		while (actionIterator.hasNext()) {
			PolicyTriple action = actionIterator.next();

			validatePolicyElement("Action operator", action.getOperator(),
					errList);
			validatePolicyElement("Action value", action.getValue(), errList);
			validatePolicyElement("Action variable", action.getVariable(),
					errList);
		}

		if (!errList.isEmpty()) {
			return false;
		}

		return errList.isEmpty();
	}

	private void validatePolicyElement(String policyElementName,
			String policyElementValue, Collection<String> errList) {

		if (policyElementValue == null || policyElementValue.equals("")) {
			errList.add("Policy element " + policyElementName + " is NULL");

			// }else if(modelPolicy.getOWLIndividual(policyElementValue) ==
			// null){
		} else if (modelFinal.getOWLIndividual(policyElementValue) == null) {

			errList.add("Specified element for " + policyElementName
					+ " does not exist in ontlogy: " + policyElementValue);
		}
	}

	/**
	 * prepares data for update policy types process
	 * 
	 * @param policyTypes
	 *            list of policy types defined in policy.types.definition.xml
	 *            file
	 */
	public void prepareUpdatePoliciesTypesProcess(
			Collection<SwrlPolicyType> policyTypes) {

		SwrlWrapper swrlWrapper = new SwrlWrapper();
		// swrlWrapper.generatePolicyTypes(modelPolicy, policyTypes);
		swrlWrapper.generatePolicyTypes(modelFinal, policyTypes);
	}

	public long addClassesToTheOntology(long value) {

		if (modelFinal != null) {

			long pos = modelFinal.getClsCount() + 1;
			if(dummyClassPosition == -1){
				dummyClassPosition = pos;
			}

			for (int i = 0; i < value; i++) {

				createOwlClass(dummyClassName
						+ (pos + i));
			}
			return modelFinal.getClsCount();
		} else {
			return -1;
		}
	}

	public long removeAllDummyClasses(){

		long dummyCnt = dummyClassPosition;

		if(modelFinal != null){

			dummyClassPosition = -1;

			boolean dummyFound = false;

			while(true){

				String className = dummyClassName + dummyCnt;
				OWLNamedClass dummyClass = getClass(modelFinal, className);
				if(dummyClass != null){
					modelFinal.deleteCls(dummyClass);
					dummyFound = true;
				}else if(dummyFound || (!dummyFound && dummyCnt > 5000)){
					break;
				}
				dummyCnt++;
			}
		}

		return dummyCnt;
	}

	@SuppressWarnings("unchecked")
	public long removeAllDummyInstances(){

		String dummyName = PoldConstants.DummyInstanceName;
		long dummyCnt = 0;

		if(modelFinal != null){

			OWLNamedClass policyClass = getClass(modelFinal, "Policy");
			if(policyClass != null){
				Collection<OWLIndividual> instances = policyClass.getInstances(true);
				if(instances != null){
					List<OWLIndividual> dummyInstances = new ArrayList<OWLIndividual>();
					for (OWLIndividual individual : instances) {
						if(individual.getName().startsWith(dummyName)){
							dummyInstances.add(individual);
							dummyCnt++;
						}
					}
					for (OWLIndividual dummy : dummyInstances) {
						modelFinal.deleteInstance(dummy);
					}
				}
			}
		}

		return dummyCnt;
	}
}
