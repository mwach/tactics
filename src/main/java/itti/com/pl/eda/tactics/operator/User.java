package itti.com.pl.eda.tactics.operator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * client-side user object
 * keeps information about user
 * @author marcin
 *
 */
public class User implements Serializable{

	private static final long serialVersionUID = 1L;

	private long id = -1;
	private String name = null;
	private List<Location> locations = new ArrayList<Location>();

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

	public List<Location> getLocations() {
		return locations;
	}
	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}

	@Override
	public String toString() {
		return id + ": " + name + "locs: " + locations.size();
	}
}
