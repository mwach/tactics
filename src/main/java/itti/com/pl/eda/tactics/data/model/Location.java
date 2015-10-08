package itti.com.pl.eda.tactics.data.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * transporting class used to keep information about single location
 * @author marcin
 *
 */
public class Location implements Serializable{

	private static final long serialVersionUID = 1L;

	private long id = -1;
	private String locationName = null;
	private String qosClass = null;

	/**
	 * key = ip address
	 * value = network object
	 */
	private Map<String, Network> networks = new HashMap<String, Network>();


	/**
	 * returns location id
	 * @return id
	 */
	public long getId() {
		return id;
	}

	/**
	 * sets location id
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * returns location name
	 * @return location name
	 */
	public String getLocationName() {
		return locationName;
	}

	/**
	 * sets location name
	 * @param locationName
	 */
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}


	/**
	 * return network object related to given location
	 * @return network
	 */
	public Map<String, Network> getNetworks() {
		return networks;
	}

	/**
	 * sets network object related to given location
	 * @param network
	 */
	public void setNetworks(Map<String, Network> networks) {
		this.networks = networks;
	}

	/**
	 * returns qos ontology related class name of localization
	 * @return qosClass
	 */
	public String getQosClass() {
		return qosClass;
	}

	/**
	 * sets qos ontology related class name of localization
	 * @param qoSClass
	 */
	public void setQosClass(String qoSClass) {
		this.qosClass = qoSClass;
	}

	public String toString() {
		return "id: " + id + ", name: " + locationName + ", qosClass: " + qosClass + 
			", networks: " + networks;
	}
}
