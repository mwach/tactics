package itti.com.pl.eda.tactics.data.model;

import java.io.Serializable;
import java.util.List;

import itti.com.pl.eda.tactics.policy.PolicyTriple;
import itti.com.pl.eda.tactics.policy.TimeCondition;

/**
 * transporting class used to keep information about single high-level policy
 * @author marcin
 *
 */
public class PolicyHighLevel implements Serializable{

	private static final long serialVersionUID = 1L;

	private long id = -1;

	private User author = null;
	private Ontology ontology = null;
	private String name = null;
	private String description = null;
	private String type = null;

	private PolicyTriple conditions = null;
	private TimeCondition timeConditions = null;
	private PolicyTriple actions = null;

	private List<PolicyIntermediateLevel> children = null;

	/**
	 * returns policy id
	 * @return id
	 */
	public long getId() {
		return id;
	}

	/**
	 * sets policy id
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * returns ontology related to current policy
	 * @return ontology
	 */
	public Ontology getOntology() {
		return ontology;
	}

	/**
	 * sets ontology related to current policy
	 * @param ontology
	 */
	public void setOntology(Ontology ontology) {
		this.ontology = ontology;
	}

	/**
	 * sets policy author
	 * @param user
	 */
	public void setAuthor(User user){
		author = user;
	}

	/**
	 * returns policy author
	 * @return author
	 */
	public User getAuthor(){
		return author;
	}

	/**
	 * sets policy name
	 * @param name
	 */
	public void setName(String name){
		this.name = name;
	}

	/**
	 * returns policy name
	 * @return name
	 */
	public String getName(){
		return name;
	}

	/**
	 * returns policy conditions
	 * @return conditions
	 */
	public PolicyTriple getConditions(){
		return conditions;
	}

	/**
	 * sets policy conditions
	 * @param conditions
	 */
	public void setConditions(PolicyTriple conditions){
		this.conditions = conditions;
	}

	/**
	 * returns policy actions
	 * @return actions
	 */
	public PolicyTriple getActions(){
		return actions;
	}

	/**
	 * sets policy actions
	 * @param actions
	 */
	public void setActions(PolicyTriple actions){
		this.actions = actions;
	}

	/**
	 * return policy time conditions
	 * @return timeConditions
	 */
	public TimeCondition getTimeConditions(){
		return timeConditions;
	}

	/**
	 * sets policy time conditions
	 * @param timeConditions
	 */
	public void setTimeConditions(TimeCondition timeConditions){
		this.timeConditions = timeConditions;
	}

	/**
	 * return policy description
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * sets policy description
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * sets policy swrl-defined type
	 * @param type
	 */
	public void setType(String type){
		this.type = type;
	}

	/**
	 * returns policy swrl-defined type
	 * @return type
	 */
	public String getType(){
		return type;
	}

	/**
	 * sets related il-policies
	 * @param children
	 */
	public void setChildren(List<PolicyIntermediateLevel> children){
		this.children = children;
	}

	/**
	 * returns related il-policies
	 * @return children
	 */
	public List<PolicyIntermediateLevel> getChildren(){
		return children;
	}



	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public String toString() {
		return "id: " + id + ":\n" +
			"Conditions: " + (getConditions() != null ? getConditions().toString() : "NULL") + "\n" + 
			"Time conditions: " + (getTimeConditions() != null ? getTimeConditions().toString() : "NULL") + "\n" +
			"Actions: " + (getActions() != null ? getActions().toString() : "NULL");
	}
}
