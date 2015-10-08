package itti.com.pl.eda.tactics.policy.impl;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import itti.com.pl.eda.tactics.policy.HLPolicy;
import itti.com.pl.eda.tactics.policy.LogicalOperator;
import itti.com.pl.eda.tactics.policy.PolicyTriple;
import itti.com.pl.eda.tactics.policy.PolicyTripleOperator;
import itti.com.pl.eda.tactics.policy.PolicyTripleVariable;
import itti.com.pl.eda.tactics.policy.TimeCondition;


/**
 * high-level policy object
 *
 */
public class HLPolicyImpl implements HLPolicy, Serializable{

	private static final long serialVersionUID = 1L;


	private long id = -1;
	private String name = null;
	private String author = null;

	private PolicyTriple conditions = new PolicyTripleImpl();
	private PolicyTriple actions = new PolicyTripleImpl();
	private TimeCondition timeConditions = null;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public void setAuthor(String s){
		author = s;
	}

	public String getAuthor(){
		return author;
	}


	public void addAction(PolicyTripleVariable actVar, PolicyTripleOperator actOper, String actVal) {

		PolicyTriple actSection = new PolicyTripleImpl(actVar, actOper, actVal);
		actions.addElement(actSection);
	}

	public void addAction(PolicyTriple action) {
		actions.addElement(action);
	}

	public void addCondition(PolicyTripleVariable condVar, PolicyTripleOperator condOper,
			String condVal) {
		conditions.addElement(new PolicyTripleImpl(condVar, condOper, condVal));
	}

	public void addCondition(PolicyTriple condition) {
		conditions.addElement(condition);
	}

	public PolicyTriple getCondition(int i) {
		if(getConditionLength() > i && i>=0){
			return conditions.getElements().get(i);
		}else{
			return null;
		}
	}

	public int getConditionLength() {
		return conditions == null ? 0 : conditions.getElementsLength();
	}

	public PolicyTriple getAction(int i) {
		if(getActionLength() > i && i>=0){
			return actions.getElements().get(i);
		}else{
			return null;
		}
		
	}
	public int getActionLength() {
		return actions != null ? actions.getElementsLength() : 0;
	}
	public PolicyTriple getActions() {
		return actions;
	}
	public PolicyTriple getConditions() {
		return conditions;
	}

	public void setConditions(PolicyTriple pCond){
		conditions = pCond;
	}
	public void setActions(PolicyTriple pAct){
		actions = pAct;
	}

	public void setTimeConditions(TimeCondition tc){
		timeConditions = tc;
	}
	public TimeCondition getTimeConditions(){
		return timeConditions;
	}

	public void addTimeCondition(TimeCondition tc){
		if(timeConditions == null){
			timeConditions = new TimeCondition(LogicalOperator.And);
		}
		if(tc != null){
			timeConditions.addCondition(tc);
		}
	}

	public int getTimeConditionsLength(){
		return timeConditions == null ? 0 : timeConditions.getConditions().size();
	}

	public TimeCondition getTimeCondition(int i){
		if(timeConditions != null && timeConditions.getConditions().size() > i && i>=0){
			return timeConditions.getConditions().get(i);
		}else{
			return null;
		}
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();

		sb.append(name + "\n");

		sb.append("\tCONDITIONS:\n");
		if(conditions != null){
			sb.append(conditions.toString() + "\n");
		}

		sb.append("\tTIME CONDITIONS:\n");
		if(timeConditions != null){
			sb.append(timeConditions.toString() + "\n");
		}

		sb.append("\tACTIONS:\n");
		if(actions != null){
			sb.append(actions.toString() + "\n");
		}

		return sb.toString();
	}

	public boolean equals(Object obj, boolean checkPolicyName) {

		if((obj != null) && (obj instanceof HLPolicyImpl)){

			HLPolicyImpl objPolicy = (HLPolicyImpl)obj;

			if(checkPolicyName){
				if((getName() == null && objPolicy.getName() == null) ||
						(getName() != null && objPolicy.getName() != null && getName().equals(objPolicy.getName()))){
				}else{
					return false;
				}
			}

			return getConditions().equals(objPolicy.getConditions()) && getActions().equals(objPolicy.getActions()) &&
				((getTimeConditions() == null && objPolicy.getTimeConditions() == null) || (getTimeConditions().equals(objPolicy.getTimeConditions())));
		}else{
			return false;
		}
	}


	@Override
	public boolean equals(Object obj) {
		return equals(obj, true);
	}


	public List<String[]> getActionsFormList() {

		List<String[]> list = new ArrayList<String[]>();

		if(actions != null && !actions.getElements().isEmpty()){

			for (PolicyTriple element : actions.getElements()) {
				list.add(new String[]{element.getVariable(), element.getOperator(), element.getValue()});
			}
		}
		return list;
	}


	public List<String[]> getConditionsFormList() {

		List<String[]> list = new ArrayList<String[]>();

		if(conditions != null && !conditions.getElements().isEmpty()){

			for (PolicyTriple element : conditions.getElements()) {
				list.add(new String[]{element.getVariable(), element.getOperator(), element.getValue()});
			}
		}
		return list;
	}


	public List<TimeCondition> getTimeConditionsList() {

		if(timeConditions != null){

			return timeConditions.getConditions();

		}else{

			return null;

		}

	}
}
