package itti.com.pl.eda.tactics.ontology.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.SimpleInstance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.query.QueryResults;
import itti.com.pl.eda.tactics.data.model.QosParameter;
import itti.com.pl.eda.tactics.exception.QosPropertyException;
import itti.com.pl.eda.tactics.exception.QosPropertyException.QosPropertyExceptionType;
import itti.com.pl.eda.tactics.policy.PolicyTripleVariable;
import itti.com.pl.eda.tactics.utils.PoldConstants;
import itti.com.pl.eda.tactics.utils.StringUtils;


/**
 * class used for searching information in ontologies by usage of spaqrl query language
 * @author marcin
 *
 */
public class SPARQLWrapper {


	/**
	 * returns list of all instances (direct as well as indirect) belonging to the specified ontology class
	 * @param model owl ontology model
	 * @param className name of the ontology class
	 * @return list of class instances
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getAllInstances(JenaOWLModel model, String className){

        List<String> resultList = new ArrayList<String>();

		if(model == null || className == null || className.equals("")){
			return resultList;
		}

		String namespace = PoldConstants.NamespaceOntologyPolicy;
		String var = "var";

        String query =  
        	"PREFIX test: <" + namespace + ">" +
        	" SELECT ?" + var + " WHERE {" +
        		"{?" + var + " rdf:type ?x. ?x rdfs:subClassOf test:" + className + "} " +
        				"UNION " +
        		"{?" + var + " rdf:type test:" + className + "}" + 
        	"}";

        try{
        	 QueryResults results = model.executeSPARQLQuery(query);
        	 while(results.hasNext()){
        		 Map<String, SimpleInstance> rsRow = (Map<String, SimpleInstance>) results.next();
        		 String text = rsRow.get(var).getBrowserText();
        		 if(!StringUtils.isNullOrEmpty(text) && text.indexOf(':') != -1){
        			 text = text.substring(text.lastIndexOf(':') + 1);
        		 }
        		 resultList.add(text);
        	 }

        } catch (Exception e) {
			resultList.clear();
		}
        return resultList;
	}


	/**
	 * returns list of direct-only instances belonging to the specified ontology class
	 * @param model owl ontology model
	 * @param className name of the ontology class
	 * @return list of class instances
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getInstances(JenaOWLModel model, String className){

        List<String> resultList = new ArrayList<String>();

		if(model == null || className == null || className.equals("")){
			return resultList;
		}

		String namespace = PoldConstants.NamespaceOntologyPolicy;
		String var = "var";

        String query =  
        	"PREFIX test: <" + namespace + ">" +
        	" SELECT ?" + var + " WHERE {" +
    		"{?" + var + " rdf:type test:" + className + "}" + 
        	"}";

        try{
        	 QueryResults results = model.executeSPARQLQuery(query);
        	 while(results.hasNext()){
        		 Map<String, SimpleInstance> rsRow = (Map<String, SimpleInstance>) results.next();
        		 String text = rsRow.get(var).getBrowserText();
        		 if(!StringUtils.isNullOrEmpty(text) && text.indexOf(':') != -1){
        			 text = text.substring(text.lastIndexOf(':') + 1);
        		 }
        		 resultList.add(text);
        	 }

        } catch (Exception e) {
        	e.printStackTrace();
			resultList.clear();
		}
        return resultList;
	}


	/**
	 * returns list of subclasses of the specified ontology class
	 * @param model owl ontology model
	 * @param className name of the ontology class
	 * @return list of subclasses
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getSubclasses(JenaOWLModel model, String className){

        List<String> resultList = new ArrayList<String>();

		if(model == null || className == null || className.equals("")){
			return resultList;
		}

		String namespace = PoldConstants.NamespaceOntologyPolicy;
		String var = "var";

        String query =  
        	"PREFIX test: <" + namespace + ">" +
        	" SELECT ?" + var + " WHERE {" +
        		"{?" + var + " rdfs:subClassOf test:" + className + "} " +
        	"}";

        try{
        	 QueryResults results = model.executeSPARQLQuery(query);
        	 while(results.hasNext()){
        		 Map<String, OWLNamedClass> rsRow = (Map<String, OWLNamedClass>) results.next();
        		 String text = rsRow.get(var).getBrowserText();
        		 if(!StringUtils.isNullOrEmpty(text) && text.indexOf(':') != -1){
        			 text = text.substring(text.lastIndexOf(':') + 1);
        		 }
        		 resultList.add(text);
        	 }

        } catch (Exception e) {
        	e.printStackTrace();
			resultList.clear();
		}
        return resultList;
	}


	/**
	 * returns a list of all locations related to the user specified by his name
	 * @param model owl ontology model
	 * @param userName name of the user
	 * @return list of all user-related locations
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getUserLocations(JenaOWLModel model, String userName) {

		 String nameSpace = PoldConstants.NamespaceOntologyQoS;

		 String root = "Location";
		 Collection<DefaultOWLNamedClass> subs = model.getSubclasses(model.getOWLNamedClass(root));

		 String actRoot = "Actor";

			List<String> locations =  new ArrayList<String>();

			Iterator<DefaultOWLNamedClass> classIter = subs.iterator();
			while(classIter.hasNext()){
				String className = classIter.next().getName();

				String var = "var";

				String query = "PREFIX ns: <" + nameSpace + "> " +
					"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
					"SELECT ?" + var + " ?prop WHERE { " +
					"?var rdf:type ns:" + className + ". " +
					"?prop rdfs:range ns:" + actRoot + ". " +
					"?var ?prop ns:" +  userName + 
					"}";

				try{
					QueryResults rs = model.executeSPARQLQuery(query);
					while(rs.hasNext()){

						Map<String, Object> resSet = (Map<String, Object>) rs.next();
						DefaultOWLObjectProperty prop = (DefaultOWLObjectProperty)resSet.get("prop");
						RDFResource vas = prop.getRange();
						if(!vas.getName().equals(actRoot)){
							continue;
						}
						DefaultOWLIndividual loc = (DefaultOWLIndividual)resSet.get(var);
						RDFSClass parent = loc.getRDFType();
		        		 String text = parent.getBrowserText();
		        		 if(!StringUtils.isNullOrEmpty(text) && text.indexOf(':') != -1){
		        			 text = text.substring(text.lastIndexOf(':') + 1);
		        		 }
						if(!locations.contains(text)){
							locations.add(text);
						}
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			return locations;
		}


	/**
	 * returns a list of all user-related networks with their qos parameters
	 * @param modelQos owl ontology model
	 * @param userName name of the user
	 * @param logicalLocation logical name of the location
	 * @return list of all user-related networks with their qos parameters
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Collection<QosParameter>> getUserNetworks(JenaOWLModel modelQos,
			String userName) {

		Map<String, Collection<QosParameter>> res = new Hashtable<String, Collection<QosParameter>>();

		String ns = PoldConstants.NamespaceOntologyQoS;

		String query = "PREFIX ns: <" + ns + "> " +
			"SELECT ?net ?loc ?qosLevel WHERE { " +
			"?loc ns:Location-hasNetworkSupport ?net. " +
			"?loc ?prop ns:" + userName + ". " +
			"?net ns:Network-hasCos_Support ?qosLevel. " +
			"}";
		try{
			QueryResults rs = modelQos.executeSPARQLQuery(query);
			while(rs.hasNext()){
				Map<String, SimpleInstance> item = rs.next();
				SimpleInstance network = item.get("net");
				SimpleInstance qosLevel = item.get("qosLevel");

				List<QosParameter> qosParams = getQosParams(modelQos, qosLevel);
				String netName = network.getName();
				netName = StringUtils.removePrefix(netName);
				res.put(netName, qosParams);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return res;
	}


	@SuppressWarnings("unchecked")
	private static List<QosParameter> getQosParams(JenaOWLModel modelQos, SimpleInstance qosLevel){

		List<QosParameter> res = new ArrayList<QosParameter>();

		String className = qosLevel.getName();
		String prefix = StringUtils.getPrefix(className);
		className = StringUtils.removePrefix(className);
		Collection<SimpleInstance> c = qosLevel.getReachableSimpleInstances();

		for (SimpleInstance qosParam : c) {
			String parentName = qosParam.getDirectType().getName();
			String attrName = qosParam.getName();

			parentName = StringUtils.removePrefix(parentName);
			attrName = StringUtils.removePrefix(attrName);

			if(!attrName.equals(className)){
				if(parentName.indexOf('_') != -1){
					parentName = parentName.substring(parentName.lastIndexOf('_') + 1);
				}
				QosParameter param = null;
				try{
					if (StringUtils.isNullOrEmpty(parentName)){
						throw new QosPropertyException(QosPropertyExceptionType.PropertyNameIsNull);
					}
					PolicyTripleVariable variable = PolicyTripleVariable.getValue(parentName);
					if (variable == null){
						//eg reliability will be skipped
//						throw new QosPropertyException(QosPropertyExceptionType.InvalidPropertyName, parentName);
						continue;
					}else{
						param = new QosParameter(variable);
					}
				}catch (Exception e) {
					e.printStackTrace();
				}

				Instance qosInst = modelQos.getInstance(attrName);
				if(qosInst == null){
					qosInst = modelQos.getInstance(prefix + ':' + attrName);
				}
				if(qosInst != null){
					Slot slotValue = modelQos.getSlot("QoSParameter-hasValue");
					if(slotValue == null){
						slotValue = modelQos.getSlot(prefix + ":QoSParameter-hasValue");
					}
					Slot slotVariation = modelQos.getSlot("QoSParameter-hasVariation");
					if(slotVariation == null){
						slotVariation = modelQos.getSlot(prefix + ":QoSParameter-hasVariation");
					}
					Object value = qosInst.getOwnSlotValue(slotValue);
					Object variation = qosInst.getOwnSlotValue(slotVariation);
					if(value != null){
						param.setValue((int)(Double.parseDouble(value.toString())));
					}else{
						if(attrName.startsWith("Rel")){
							if(attrName.indexOf('H') != -1){
								param.setValue(2);
							}else if(attrName.indexOf('M') != -1){
								param.setValue(1);
							}else{
								param.setValue(0);
							}
						}
					}
					if(variation != null){
						param.setVariation((int)Double.parseDouble(variation.toString()));
					}
				}
				res.add(param);
			}
		}
		return res;
	}
}