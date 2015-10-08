package itti.com.pl.eda.tactics.ontology.controller;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import itti.com.pl.eda.tactics.data.model.QosParameter;
import itti.com.pl.eda.tactics.exception.RefinementCalculatorException;
import itti.com.pl.eda.tactics.exception.RefinementCalculatorException.RefinementCalculatorExceptionType;
import itti.com.pl.eda.tactics.utils.StringUtils;

/**
 * class keeps information about swrl rules used during refinement process
 * @author marcin
 *
 */
public class RefinementController {

	private static Logger logger = Logger.getLogger(RefinementController.class);

	private boolean modelUpdated = true;

	//map used for weight calculations
	private Map<String, QosClass> qosClasses = new Hashtable<String, QosClass>();


	/**
	 * initializes refinement controller
	 * load swrl rules used during refinement
	 */
	public void init(){
	}


	/**
	 * returns true, if ontology model was already updated with the swrl rules
	 * false otherwise
	 * @return true, if ontology model was already updated with the swrl rules, false otherwise
	 */
	public boolean isModelUpdated(){
		return modelUpdated;
	}


	/**
	 * calculates value of weight attribute for specified network and application type
	 * @param applicationType type of the application
	 * @param attributes set of qos attributes related to the network
	 * @return value of the weight for specified parameters
	 * @throws RefinementCalculatorException
	 */
	public int calculateWeight(String applicationType, Collection<QosParameter> attributes) throws RefinementCalculatorException {

		if(applicationType == null){
			throw new RefinementCalculatorException(RefinementCalculatorExceptionType.NullQoSParameter);
		}

		if(qosClasses.containsKey(applicationType)){

			QosClass qosClass = qosClasses.get(applicationType);
			return 	qosClass.calculateWeight(attributes);

		}else{
			throw new RefinementCalculatorException(RefinementCalculatorExceptionType.UnrecognizedApplicationType, "Parameter: " + applicationType);
		}
	}


	/**
	 * initialization of the class internal elements
	 * @param classPolicies list of all qos elements weights and their priorities for refinement process
	 * @return true, if process ends with success, false otherwise
	 * @throws RefinementCalculatorException
	 */
	public boolean constructWeightsCalculator(Collection<Object[]> classPolicies)  throws RefinementCalculatorException {

		logger.info("Constructing refinement weights calculator");

		if(classPolicies != null && !classPolicies.isEmpty()){

			Iterator<Object[]> listIter = classPolicies.iterator();
			while(listIter.hasNext()){

				try{

					Object[] arr = listIter.next();

					if(arr == null || arr.length != 4){
						throw new RefinementCalculatorException(RefinementCalculatorExceptionType.ParseException, "Item has wrong length: " + (arr == null ? "NULL" : arr.length) + ". Value 4 is required");
					}

					String qosClass = arr[0].toString();
					String qosParam = arr[1].toString();

					if(StringUtils.isNullOrEmpty(qosClass)){
						throw new RefinementCalculatorException(RefinementCalculatorExceptionType.ParseException, "QoSClass is blank");
					}
					if(StringUtils.isNullOrEmpty(qosParam)){
						throw new RefinementCalculatorException(RefinementCalculatorExceptionType.ParseException, "QoSParam is blank");
					}

					int weight = -1;
					int param = -1;

					weight = StringUtils.getIntValue(arr[2].toString());
					param = StringUtils.getIntValue(arr[3].toString());

					addPolicy(qosClass, qosParam, param, weight);

				}catch (Exception e) {
					throw new RefinementCalculatorException(RefinementCalculatorExceptionType.ParseException, e.toString());
				}
			}
		}else{
			throw new RefinementCalculatorException(RefinementCalculatorExceptionType.EmptyDataSet);
		}
		return true;
	}


	private void addPolicy(String netName, String qosParamName, int queryValue, int weight){

		QosClass currClass = null;

		if(qosClasses.containsKey(netName)){

			currClass = qosClasses.get(netName);

		}else{

			currClass = new QosClass();
			qosClasses.put(netName, currClass);

		}

		currClass.setParam(qosParamName, queryValue, weight);
	}


	/**
	 * internal class for keeping information about one set of qos parameters
	 * (related to the specified network)
	 * @author marcin
	 *
	 */
	private static class QosClass{

		private Map<String, Map<Integer, Integer>> conditions = new Hashtable<String, Map<Integer,Integer>>();

		public void setParam(String qosParamName, int queryValue, int weight) {

			Map<Integer, Integer> cond = null;

			if(conditions.containsKey(qosParamName)){
				cond = conditions.get(qosParamName);
			}else{
				cond = new Hashtable<Integer, Integer>();
				conditions.put(qosParamName, cond);
			}

			cond.put(queryValue, weight);
		}

		/**
		 * calculates value of the weight attribute for the specified network
		 * @param attributes list of qos attributes related to the examined network
		 * @return weight attribute for this network
		 * @throws RefinementCalculatorException
		 */
		public int calculateWeight(Collection<QosParameter> attributes) throws RefinementCalculatorException {

			int sum = 0;
			if(attributes != null){

				for (QosParameter qosParam : attributes) {

					String property = qosParam.getProperty().name();
					int value = qosParam.getValue();

					Map<Integer, Integer> qosMap = conditions.get(property.toUpperCase());

					if(qosMap != null){

						int retVal = -1;

						for (int classLev : qosMap.keySet()) {
							if(classLev > value){
								if(retVal == -1){
									retVal = classLev;
								}else{
									retVal = (retVal > classLev ? classLev : retVal);
								}
							}
						}
						if(retVal > 0){
							sum += qosMap.get(retVal);
						}
					}
				}

			}else{
				throw new RefinementCalculatorException(RefinementCalculatorExceptionType.NullCalculateWeight, "Given attribute is null. Weight can't be calculated");
			}
			return sum;
		}
	}

}
