package itti.com.pl.eda.tactics.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import itti.com.pl.eda.tactics.exception.ConfigException;
import itti.com.pl.eda.tactics.exception.ConfigException.ConfigExceptionType;

/**
 * class used to local pold configuration from configuration files
 * @author marcin
 *
 */
public class ConfigurationLoader {

	private Logger logger = Logger.getLogger(ConfigurationLoader.class);

	private Map<String, String> settings = new HashMap<String, String>();

	/**
	 * available configuration parameters
	 * @author marcin
	 *
	 */
	public enum ConfigurationParameters{
		//pold server parameters
		host_name, 
		host_port,
		//client name
		requester_name,

		//ports used by server to listen for clients/operators/listeners requests
		port_clients, 
		port_operators,
		port_listeners,

		//port used by listeners
		listener_port,
	};


	/**
	 * loads configuration from file
	 * @param configFileName configuration file name
	 * @return true, if settings were loaded successfully, false otherwise
	 * @throws ConfigException
	 */
	public boolean loadConfiguration(String configFileName) throws ConfigException{

		boolean fileExists = false;

		logger.info("Loading configuration: " + configFileName);

		try{
			fileExists = FileUtils.isDocumentExists(configFileName);
			if(fileExists){
				logger.info("Config file exists");
			}else{
				throw new ConfigException(ConfigExceptionType.FileNotFoundException, configFileName);
			}
		}catch (Exception e) {
			logger.warn("Exception: " + e.toString());
			throw new ConfigException(ConfigExceptionType.FileNotFoundException, e.getMessage());
		}

		if(fileExists){
			return parseDocument(configFileName);
		}else{
			throw new ConfigException(
					ConfigExceptionType.FileNotFoundException, configFileName);

		}
	}

	private boolean parseDocument(String configFileName) throws ConfigException{

		logger.info("Config file parsing...");
		settings = XmlUtils.parseProperiesDocument(configFileName);

		return true;
	}


	/**
	 * return configuration property value
	 * @param property configuration property
	 * @return property value, or null if property wasn't set
	 * @throws ConfigException
	 */
	public String getProperty(ConfigurationParameters property) throws ConfigException {

		if(settings != null && settings.containsKey(property.name())){
			return settings.get(property.name());
		}else{
			throw new ConfigException(ConfigExceptionType.MissingAttributeException, property.name());
		}
	}


	/**
	 * return configuration property value
	 * @param property configuration property
	 * @return property value, or -1 if property wasn't set
	 * @throws ConfigException
	 */
	public int getPropertyInt(ConfigurationParameters property) throws ConfigException {

		int value = -1;

		try{
			String propertyValue = getProperty(property);
			value = StringUtils.getIntValue(propertyValue);
		}catch (Exception e) {
			throw new ConfigException(ConfigExceptionType.ParseConfigAttributeException, "Property: " + property.name());
		}
		return value;
	}
}
