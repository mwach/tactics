package itti.com.pl.eda.tactics.data.model;

import java.io.Serializable;
import java.util.Set;

/**
 * transporting class used to keep information about single user
 * @author marcin
 *
 */
public class User implements Serializable{

	private static final long serialVersionUID = 1L;

	private long id = -1;
	private String name = null;

	private Set<Location> locations = null;

	/**
	 * returns user id
	 * @return id
	 */
	public long getId() {
		return id;
	}

	/**
	 * sets user id
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * returns user name
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * sets user name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * returns locations related to the user
	 * @return locations
	 */
	public Set<Location> getLocations() {
		return locations;
	}

	/**
	 * sets locations related to the user
	 * @param locations
	 */
	public void setLocations(Set<Location> locations) {
		this.locations = locations;
	}

	public String toString() {
		return "id: " + id + "name: " + name;
	}
}
