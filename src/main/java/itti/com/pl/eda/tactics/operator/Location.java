package itti.com.pl.eda.tactics.operator;

import java.io.Serializable;
import java.util.Map;

/**
 * client-side location object
 * keeps information about location
 * @author marcin
 *
 */
public class Location implements Serializable{

	private static final long serialVersionUID = 1L;

	private long id = -1;

	private String locationName = null;
	private String qosClass = null;
	private Map<String, String> networks = null;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getQosClass() {
		return qosClass;
	}
	public void setQosClass(String qosClass) {
		this.qosClass = qosClass;
	}

	public Map<String, String> getNetworks() {
		return networks;
	}
	public void setNetworks(Map<String, String> networks) {
		this.networks = networks;
	}

	@Override
	public String toString() {
		return id + ": " + locationName;
	}
}
