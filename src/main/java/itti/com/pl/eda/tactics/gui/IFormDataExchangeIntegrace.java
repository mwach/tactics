package itti.com.pl.eda.tactics.gui;

import itti.com.pl.eda.tactics.policy.PolicyTripleVariable;
import itti.com.pl.eda.tactics.policy.TimeCondition;

/**
 * interface used to exchange data/events between main form and modal dialog
 * @author marcin
 *
 */
public interface IFormDataExchangeIntegrace {

	/**
	 * event created when values on time conditions dialog were changed
	 * @param timeCondition affected time condition
	 */
	public void processTimeCondition(TimeCondition timeCondition);

	/**
	 * event created when values on conditions dialog were changed
	 * @param states new states of condition controls
	 */
	public void processUpdateConditions(boolean[] states);

	/**
	 * event created when values on time conditions dialog were changed
	 * @param states new states of time condition controls
	 */
	public void processUpdateTimeConditions(boolean[] states);

	/**
	 * event created when values on actions dialog were changed
	 * @param states new states of actions controls
	 */
	public void processUpdateActions(boolean[] states);

	public void setActions(PolicyTripleVariable[] vars, int[] values);
}
