package itti.com.pl.eda.tactics.ontology.swrl;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.swrl.bridge.BridgeFactory;
import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLRuleEngineBridge;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParseException;
import itti.com.pl.eda.tactics.exception.SwrlEngineException;
import itti.com.pl.eda.tactics.exception.SwrlEngineException.SwrlEngineExceptionType;

/**
 * class wraps all swrl-related operations
 * @author marcin
 *
 */
public class SwrlWrapper {

	Logger logger = Logger.getLogger(SwrlWrapper.class);


	/**
	 * merges swrl-language defined policies with ontology model
	 * modifies in-ontology stored policy types with swrl defined type
	 * @param owlModel owl ontology model
	 * @param swrlPolicyTypes list of swrl defined policies
	 * @return true, if process ends with success, false otherwise
	 */
	public boolean generatePolicyTypes(JenaOWLModel owlModel, Collection<SwrlPolicyType> swrlPolicyTypes){

		logger.info("Updating Model with new policy types");

		boolean result = false;

		try{

			if(owlModel == null){
				logger.warn("OWL ontology model is NULL");
				throw new SwrlEngineException(SwrlEngineExceptionType.OntologyModelIsNull);
			}

			if(swrlPolicyTypes == null || swrlPolicyTypes.isEmpty()){
				logger.warn("There are no policy type definitions");
				throw new SwrlEngineException(SwrlEngineExceptionType.NoPolicyTypes);
			}

			//add property policyType to OWL model
			String typeProperty = "Policy-PolicyType";

			if(owlModel.getOWLProperty(typeProperty) == null){

				logger.info("Adding required property: " + typeProperty);

				OWLDatatypeProperty property = owlModel.createOWLDatatypeProperty(typeProperty);

				RDFSNamedClass policyClass = owlModel.getRDFSNamedClass("Policy");
				if(policyClass == null){
					logger.warn("OWL class POLICY is missing in given model");
					throw new SwrlEngineException(SwrlEngineExceptionType.MissingClass, "Policy");
				}

				property.setDomain(policyClass);
				property.setRange(owlModel.getRDFSNamedClass("String"));

			}

			logger.info("Creating SwrlFactory object");
			SWRLFactory factory = new SWRLFactory(owlModel);

			for (SwrlPolicyType swrlPolicyType : swrlPolicyTypes) {

				String query = swrlPolicyType.getSwrlQuery();
				String policyName = swrlPolicyType.getName();

				String replacement = "[?X]";
				while(true){

					if(query.indexOf(replacement) == -1){
						break;
					}

					int pos = query.indexOf(replacement);
					query = query.substring(0, pos) + "\"" + policyName + "\"" + query.substring(pos + replacement.length());
				}

				logger.info("Implementing query in OWL model: " + query);

				factory.createImp(query);
			}

			logger.info("Swrl Rule bridge will be runned now...");

			try {
				SWRLRuleEngineBridge bridge = BridgeFactory.createBridge("SWRLJessBridge", owlModel);

				logger.info("Bridge infer");

				bridge.infer();

				logger.info("Getting updated model from Swrl ridge");

				owlModel = (JenaOWLModel)bridge.getOWLModel();

			} catch (SWRLRuleEngineBridgeException e) {
				logger.warn("Error during using SWRL bridge: " + e.toString());
				throw new SwrlEngineException(SwrlEngineExceptionType.SwrlBridgeException);
			}

			result = true;

		}catch (Exception e) {
			logger.warn("Errors occur inside generatePolicyTypes method:\n" + e.toString());
		}

		return result;
	}


	/**
	 * updates ontology model with swrl rules
	 * @param owlModel owl ontology model
	 * @param swrlRules list of swrl rules
	 * @return true, if process ends with success, false otherwise
	 */
	public boolean updateModelWithSwrlRule(JenaOWLModel owlModel, List<String> swrlRules){

		logger.info("Updating ontology model with swrl rules");

		if(owlModel != null && swrlRules != null){

			SWRLFactory factory = new SWRLFactory(owlModel);

			for (String rule : swrlRules) {
				try {

					factory.createImp(rule);
					logger.debug("Rule " + rule + " added suffessfully");

				} catch (SWRLParseException e) {
					logger.warn("Fail at adding rule: " + rule);
				}
			}

			logger.info("Swrl Rule bridge will be runned now...");

			boolean result = false;

			try {
				SWRLRuleEngineBridge bridge = BridgeFactory.createBridge("SWRLJessBridge", owlModel);

				logger.info("Bridge infer");

				bridge.infer();

				logger.info("Getting updated model from Swrl ridge");

				owlModel = (JenaOWLModel)bridge.getOWLModel();
				result = true;

			} catch (SWRLRuleEngineBridgeException e) {
				logger.warn("Error during using SWRL bridge: " + e.toString());
			}

			return result;

		}else{
			logger.warn("Cannot update model with swrl rules. One of them is null or empty");
			return false;
		}
	}


	public boolean runSwrlRules(JenaOWLModel owlModel, Collection<String> errors) {

		logger.info("Updating ontology model with internal swrl rules");

		boolean result = false;

		if(owlModel != null){

			logger.info("Swrl Rule bridge will be runned now...");

//			setDate(false);

			try {
				SWRLRuleEngineBridge bridge = BridgeFactory.createBridge("SWRLJessBridge", owlModel);

				logger.info("Bridge infer");

				bridge.infer();

				logger.info("Getting updated model from Swrl ridge");

				owlModel = (JenaOWLModel)bridge.getOWLModel();
				result = true;

			} catch (SWRLRuleEngineBridgeException e) {
				logger.warn("Error during using SWRL bridge: " + e.toString());
				errors.add("Error during using SWRL bridge: " + e.toString());
			}

//			setDate(true);

			return result;

		}else{
			logger.warn("Cannot update model with swrl rules. One of them is null or empty");
			errors.add("Cannot update model with swrl rules. One of them is null or empty");
			return result;
		}
	}

	private static String runDate = null;

	void setDate(boolean curr) {

		if(runDate == null){
			int year = Calendar.getInstance().get(Calendar.YEAR) - 2000;
			int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
			int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

			runDate =  (year < 10 ? ("0" + year) : year) + "-" + (month < 10 ? ("0" + month) : month) + "-" + day;
		}
		String newDate = null;
		if(curr){
			newDate = runDate;
		}else{
			newDate = "09-01-01";
		}

		try {
			String input = "date " + newDate;
			Process process = Runtime.getRuntime().exec(input);
			StringBuffer res = new StringBuffer();
			int sByte = -1;
			while((sByte = process.getErrorStream().read()) != -1){
				res.append((char)sByte);
			}
			process.getErrorStream().close();
			while((sByte = process.getInputStream().read()) != -1){
				res.append((char)sByte);
			}
			process.getInputStream().close();
			process.getOutputStream().close();
			process.destroy();
			Thread.sleep(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
