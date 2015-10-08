package itti.com.pl.eda.tactics.data.model;

import java.io.Serializable;

import itti.com.pl.eda.tactics.ontology.controller.OntologyType;

/**
 * transporting class used to keep information about single ontology
 * @author marcin
 *
 */
public class Ontology implements Serializable{

	private static final long serialVersionUID = 1L;

	private long id = -1;
	private String ontologyName = null;
	private String ontologyLocation = null;
	private boolean _default = false;
	private OntologyType ontologyType = null;


	/**
	 * returns ontology id
	 * @return ontology id
	 */
	public long getId() {
		return id;
	}

	/**
	 * sets ontology id
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * returns ontology name
	 * @return ontology name
	 */
	public String getOntologyName() {
		return ontologyName;
	}

	/**
	 * sets ontology name
	 * @param ontologyName
	 */
	public void setOntologyName(String ontologyName) {
		this.ontologyName = ontologyName;
	}

	/**
	 * returns ontology location (ontology repository location)
	 * @return ontology location
	 */
	public String getOntologyLocation() {
		return ontologyLocation;
	}

	/**
	 * sets ontology location (ontology repository location)
	 * @param ontologyLocation
	 */
	public void setOntologyLocation(String ontologyLocation) {
		this.ontologyLocation = ontologyLocation;
	}

	/**
	 * returns ontology type (policy or qos type)
	 * @return ontologyType
	 */
	public OntologyType getOntologyType(){
		return ontologyType;
	}

	/**
	 * sets ontology type (policy or qos type)
	 * @param ontologyType
	 */
	public void setOntologyType(OntologyType ontologyType) {
		this.ontologyType = ontologyType;
	}

	/**
	 * returns true if ontology is default (currently in use)
	 * @return true if ontology is used, false otherwise
	 */
	public boolean isDefault(){
		return _default;
	}

	/**
	 * sets current state of the ontology
	 * @param _default sould be true if ontology is used, false otherwise
	 */
	public void setDefault(boolean _default){
		this._default = _default;
	}


	public int getQosOntology(){
		switch(ontologyType){
			case QoSOntology:
				return 1;
			case PolicyOntology:
				return 0;
			default:
				return 2;
		}
	}

	public void setQosOntology(int isQos){
		if(isQos == 1){
			ontologyType = OntologyType.QoSOntology;
		}else if(isQos == 0){
			ontologyType = OntologyType.PolicyOntology;
		}else{
			ontologyType = OntologyType.FinalOntology;
		}
	}


	@Override
	public String toString() {
		return "id: " + id + ", name: " + ontologyName + ", " +
				"location: " + ontologyLocation + ", " +
				"type: " + ontologyType + ", default: " + _default;
	}

}
