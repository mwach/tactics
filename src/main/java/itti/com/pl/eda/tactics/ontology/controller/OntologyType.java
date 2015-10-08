package itti.com.pl.eda.tactics.ontology.controller;


/**
 * enum, which contains list of available ontology types
 * @author marcin
 *
 */
public enum OntologyType {
	QoSOntology, 
	PolicyOntology,
	FinalOntology
	;

	/**
	 * returns true, if ontology is a qos ontology, false otherwise
	 * @param ontologyType ontology type
	 * @return true, if ontology is a qos ontology, false otherwise
	 */
	public static boolean isQoSOntology(OntologyType ontologyType) {
		return ontologyType != null && ontologyType == QoSOntology;
	}

	/**
	 * returns valid ontology type based on given parameter
	 * @param isQoSOntology true, if qos ontology enum should be returned
	 * @return qos ontology for 'true' argument, policy ontology otherwise
	 */
	public static OntologyType getOntologyType(boolean isQoSOntology){
		return isQoSOntology ? OntologyType.QoSOntology : OntologyType.PolicyOntology;
	}
}
