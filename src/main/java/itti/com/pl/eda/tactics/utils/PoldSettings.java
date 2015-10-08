package itti.com.pl.eda.tactics.utils;

/**
 * main pold setting like config file locations
 * @author marcin
 *
 */
public class PoldSettings {

	/**
	 * location of server configuration file
	 */
	public static final String ServerConfigurationFile = "server.config.xml";

	/**
	 * location of client configuration file
	 */
	public static final String ClientConfigurationFile = "client.config.xml";

	/**
	 * location of operator configuration file
	 */
	public static final String OperatorConfigurationFile = "operator.config.xml";

	/**
	 * location of operator configuration file
	 */
	public static final String ListenerConfigurationFile = "listener.config.xml";

	/**
	 * location of file which contains swrl-defined policy types
	 */
	public static final String PolicyTypesDefinitionsFile = "policy.types.definition.xml";

	/**
	 * prefix used to distinguish client messages from operator messages
	 */
	public static final String OperatorPrefix = "OP_";

	/**
	 * how refinement should be done
	 */
	public static RefinementMethods RefinementMethod = RefinementMethods.SwrlBased;

}
