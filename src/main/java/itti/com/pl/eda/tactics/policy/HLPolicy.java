package itti.com.pl.eda.tactics.policy;

import java.util.List;

/**
 * interface of the high-level policy
 * @author marcin
 *
 */
public interface HLPolicy {


	/**
	 * returns the id of the policy
	 * @return
	 */
	public long getId();


	/**
	 * returns the name of the policy
	 * @return string
	 */
	public String getName();


	/**
	 * sets the name of the policy
	 * @param name string
	 */
	public void setName(String name);


	/**
	 * returns policy's author
	 * @return
	 */
	public String getAuthor();


	/**
	 * adds a new condition to the policy
	 * @param condVar variable
	 * @param condOper operator
	 * @param condVal value
	 */
	public void addCondition(PolicyTripleVariable condVar, PolicyTripleOperator condOper, String condVal);


	/**
	 * adds a new condition to the policy
	 * @param condition
	 */
	public void addCondition(PolicyTriple condition);


	/**
	 * returns length of conditions
	 * @return int
	 */
	public int getConditionLength();


	/**
	 * returns specified condition
	 * @param i int
	 * @return PolicyTriple objects
	 */
	public PolicyTriple getCondition(int i);


	/**
	 * returns all conditions
	 * @return PolicyTriple objects
	 */
	public PolicyTriple getConditions();


	/**
	 * adds a new action to the policy
	 * @param variable
	 * @param operator
	 * @param value
	 */
	public void addAction(PolicyTripleVariable variable, PolicyTripleOperator operator, String value);


	/**
	 * adds a new action to the policy
	 * @param action
	 */
	public void addAction(PolicyTriple action);


	/**
	 * returns length of actions
	 * @return int
	 */
	public int getActionLength();


	/**
	 * returns specified action
	 * @param i int
	 * @return PolicyTriple objects
	 */
	public PolicyTriple getAction(int i);


	/**
	 * returns all actions
	 * @return PolicyTriple objects
	 */
	public PolicyTriple getActions();


	/**
	 * adds a new time condition to the policy
	 * @param timeCondition new time condition
	 */
	public void addTimeCondition(TimeCondition timeCondition);


	/**
	 * returns length of time conditions
	 * @return int
	 */
	public int getTimeConditionsLength();


	/**
	 * returns specified time condition
	 * @param i int
	 * @return TimeCondition objects
	 */
	public TimeCondition getTimeCondition(int i);


	/**
	 * returns all time conditions
	 * @return TimeCondition objects
	 */
	public TimeCondition getTimeConditions();


	/**
	 * returns a list of actions if form of list of string arrays[var, oper, val] to fill the form
	 * @return
	 */
	public List<String[]> getActionsFormList();

	/**
	 * returns a list of actions if form of list of string arrays[var, oper, val] to fill the form
	 * @return
	 */
	public List<String[]> getConditionsFormList();

	/**
	 * returns a list of actions if form of list of string arrays[var, oper, val] to fill the form
	 * @return
	 */
	public List<TimeCondition> getTimeConditionsList();


	/**
	 * compares two policies
	 * @param object HLPolicy object to compare
	 * @param compareName indicates if policy name also should be compared
	 * @return true if policies are equal, false otherwise
	 */
	public boolean equals(Object object, boolean compareName);
}