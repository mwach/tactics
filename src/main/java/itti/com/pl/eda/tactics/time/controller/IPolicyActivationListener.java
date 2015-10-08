package itti.com.pl.eda.tactics.time.controller;

import java.util.List;

/**
 * an interface to manipulate policies activation statuses
 * @author marcin
 *
 */
public interface IPolicyActivationListener {

	/**
	 * activates policy
	 * @param policyId id of the policy
	 * @param errors possible errors
	 * @return true, if process ends with success, false otherwise
	 */
	public boolean activatePolicy(long policyId, List<String> errors);

	/**
	 * deactivates policy
	 * @param policyId id of the policy
	 * @param errors possible errors
	 * @return true, if process ends with success, false otherwise
	 */
	public boolean deactivatePolicy(long policyId, List<String> errors);
}
