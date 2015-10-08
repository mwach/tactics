package itti.com.pl.eda.tactics.ontology.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;


import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLProperty;
import itti.com.pl.eda.tactics.data.model.PolicyData;
import itti.com.pl.eda.tactics.ontology.model.UserDataCache;
import itti.com.pl.eda.tactics.ontology.model.UserDataImpl;
import itti.com.pl.eda.tactics.policy.impl.PolicyElement;
import itti.com.pl.eda.tactics.utils.PoldConstants;
import itti.com.pl.eda.tactics.utils.StringUtils;

/**
 * class used to manage ontologies
 * allows to perform various actions on ontologies
 * @author marcin
 *
 */
public class OntologyManager {

	Logger logger = Logger.getLogger(OntologyManager.class);

//	JenaOWLModel modelPolicy = null;
//	JenaOWLModel modelQos = null;
	JenaOWLModel modelFinal = null;


	/**
	 * merges ontology model with additional data
	 * @param policyData additional ontology data
	 */
	public void fillOntologyWithDbData(List<PolicyData> policyData){

		if(policyData != null){
			for (PolicyData policyDataItem : policyData) {
				String className = policyDataItem.getClassName();
				String value = policyDataItem.getValue();
				Map<String, List<String>> properties = policyDataItem.getProperties();
				if(StringUtils.isNullOrEmpty(value)){
					policyDataItem.setValue(null);
				}else{
//					PolicyManager.createSimpleInstance(modelPolicy, className, value, properties);
//					PolicyManager.createSimpleInstance(modelQos, className, value, properties);
					PolicyManager.createSimpleInstance(modelFinal, className, value, properties);
				}
			}
		}
	}


	/**
	 * loads from ontology data related to the user based on database information
	 * @param policyData database information about user classes and instances
	 * @return cache object containing user-related data
	 */
	public UserDataCache getUserPolicyElements() {

		UserDataCache cache = new UserDataCache();

		for (PolicyElement policyElement : PolicyElement.values()) {

			if(policyElement == PolicyElement.ConditionVariable || policyElement == PolicyElement.ActionVariable){

				String searchedName = getOntologyClassName(policyElement);

				UserDataImpl condClass = new UserDataImpl(policyElement, null);

//				List<String> varClasses = SPARQLWrapper.getSubclasses(modelPolicy, searchedName);
				List<String> varClasses = SPARQLWrapper.getSubclasses(modelFinal, searchedName);
				for (String mainClass : varClasses) {
//					List<String> instances = SPARQLWrapper.getInstances(modelPolicy, mainClass);
					List<String> instances = SPARQLWrapper.getInstances(modelFinal, mainClass);
//					List<String> values = SPARQLWrapper.getInstances(modelPolicy, mainClass.replaceAll("Variable", "Value"));
					List<String> values = SPARQLWrapper.getInstances(modelFinal, mainClass.replaceAll("Variable", "Value"));
					if(instances != null){
						for (String instance : instances) {
							UserDataImpl item = new UserDataImpl(policyElement, instance);
							if(values != null){
								for (String string : values) {
									item.addInstance(string);
								}
							}
							condClass.addChild(item);
						}
					}
				}
				cache.addItem(condClass);
			}else if(policyElement == PolicyElement.ConditionOperator || policyElement == PolicyElement.ActionOperator){
				String searchedName = getOntologyClassName(policyElement);
				UserDataImpl condClass = new UserDataImpl(policyElement, "Operator");
//				List<String> itemsList = SPARQLWrapper.getAllInstances(modelPolicy, searchedName);
				List<String> itemsList = SPARQLWrapper.getAllInstances(modelFinal, searchedName);
				for (String object : itemsList) {
					condClass.addInstance(object);
				}
				cache.addItem(condClass);
			}
		}
		return cache;
	}


	/**
	 * returns value of valid ontology class name
	 * @param element policy element
	 * @return name of the ontology class, which represent given element
	 */
	private String getOntologyClassName(PolicyElement element){

		if(element != null){
			switch (element) {
				case ConditionVariable:
					return PoldConstants.ConditionVariable;
				case ConditionOperator:
					return PoldConstants.ConditionOperator;
				case ConditionValue:
					return PoldConstants.ConditionValue;

				case ActionVariable:
					return PoldConstants.ActionVariable;
				case ActionOperator:
					return PoldConstants.ActionOperator;
				case ActionValue:
					return PoldConstants.ActionValue;
				default:
					return null;
			}
		}else{
			return null;
		}
	}


	/**
	 * returns all instances (from the whole branch) which belong to the class identified by its name
	 * @param className name of the ontology class
	 * @return list of instances of this class
	 */
	public List<String> getAllInstancesList(String className){
//		return SPARQLWrapper.getAllInstances(modelPolicy, className);
		return SPARQLWrapper.getAllInstances(modelFinal, className);
	}

	/**
	 * returns direct-only instances which belong to the class identified by its name
	 * @param className name of the ontology class
	 * @return list of direct instances of this class
	 */
	public List<String> getInstancesList(String className){
//		return SPARQLWrapper.getInstances(modelPolicy, className);
		return SPARQLWrapper.getInstances(modelFinal, className);
	}


	/**
	 * returns a list of subclasses (children classes) of the class identified by its name
	 * @param className name of the parent class
	 * @return list of suclasses
	 */
	public List<String> getSubclassesList(String className){
//		return SPARQLWrapper.getSubclasses(modelPolicy, className);
		return SPARQLWrapper.getSubclasses(modelFinal, className);
	}


	/**
	 * validates policy object
	 * @param policyInstance policy instance object
	 * @param errors possible errors
	 * @return true, if policy is valid, false otherwise
	 */
	@SuppressWarnings("unchecked")
	public boolean validatePolicy(OWLIndividual policyInstance, List<String> errors) {

		String parentClassName = policyInstance.getRDFType().getName();

//		Set<AbstractOWLProperty> data = modelPolicy.getOWLNamedClass(parentClassName).getAssociatedProperties();
		Set<AbstractOWLProperty> data = modelFinal.getOWLNamedClass(parentClassName).getAssociatedProperties();

		for (AbstractOWLProperty namedClass : data) {

			Collection<RDFResource> c = new ArrayList<RDFResource>();
			if(policyInstance.getPropertyValue(namedClass) instanceof RDFResource){
			RDFResource val = (RDFResource)policyInstance.getPropertyValue(namedClass);
			c.add(val);
			boolean isInvalid = isInvalid(policyInstance, namedClass, c);
			if(isInvalid){
				errors.add(namedClass.getName());
			}
			if(val instanceof OWLIndividual)
				validatePolicy((OWLIndividual)val, errors);
			}else{
				//TODO: tu trafiaja obiekty OPERATOR (int)
			}
		}
		return false;
	}


	
	private boolean isInvalid(RDFResource subject, RDFProperty predicate, Collection<RDFResource> values) {
        for (Iterator<RDFResource> it = values.iterator(); it.hasNext();) {
            Object value = it.next();
            if (!subject.isValidPropertyValue(predicate, value)) {
                return true;
            }
        }
        RDFResource type = subject.getRDFType();
        if (type instanceof OWLNamedClass) {
            OWLNamedClass namedClass = (OWLNamedClass) type;
            int min = namedClass.getMinCardinality(predicate);
            if (min >= 0 && values.size() < min) {
                return true;
            }
            int max = namedClass.getMaxCardinality(predicate);
            if (max >= 0 && values.size() > max) {
                return true;
            }
            if(values.size() == 0 && namedClass.getSomeValuesFrom(predicate) != null) {
                return true;
            }
        }
        return false;
    }


	/**
	 * sets reference to the in-memory ontology model
	 * @param ontologyType ontology descriptor
	 * @param ontologyModel Jena OWL ontology model
	 */
	public void setOntology(OntologyType ontologyType, JenaOWLModel ontologyModel) {

		switch (ontologyType) {
		case PolicyOntology:
//			modelPolicy = ontologyModel;
			break;
		case QoSOntology:
//			modelQos = ontologyModel;
			break;
		case FinalOntology:
			modelFinal = ontologyModel;
			break;
		}
	}


	public long getSizeOfTheOntology() {

//		if(modelQos != null){
		if(modelFinal != null){
//			long size = modelQos.getClsCount();
			long size = modelFinal.getClsCount();
			return size;
		}else{
			return -1;
		}
	}
}
