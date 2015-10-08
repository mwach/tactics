package itti.com.pl.eda.tactics.data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * transporting class used to keep information about single policy data
 * policy data objects are used to keep non-static ontology content
 * these information are merged later with the ontology
 * @author marcin
 *
 */
public class PolicyData {

	private long id = -1;
	private String className = null;
	private String value = null;
	private boolean instance = false;

	//objects are kept in tree-like structure
	private PolicyData parent = null;
	private Set<PolicyData> children = null;
	private Map<String, List<String>> properties = new HashMap<String, List<String>>();;

	public PolicyData(){
	}

	public PolicyData(String className, String value, boolean instance){
		this.className = className;
		this.value = value;
		this.instance = instance;
	}

	/**
	 * returns id
	 * @return id
	 */
	public long getId() {
		return id;
	}

	/**
	 * sets id
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * returns qos class name
	 * @return className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * sets qos class name
	 * @param className
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * returns instance value
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * sets instance value
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * flag, which indicates if current object is a ontology class or instance representation
	 * @param instance
	 */
	public void setInstance(boolean instance){
		this.instance = instance;
	}

	/**
	 * returns flag, which indicates if current object is a ontology class or instance representation
	 * @return instance
	 */
	public boolean isInstance(){
		return instance;
	}

	/**
	 * return parent of the current element
	 * @return parent
	 */
	public PolicyData getParent() {
		return parent;
	}

	/**
	 * sets parent of the current element
	 * @param parent
	 */
	public void setParent(PolicyData parent) {
		this.parent = parent;
	}

	/**
	 * returns children of the current element
	 * @return children
	 */
	public Set<PolicyData> getChildren() {
		return children;
	}

	/**
	 * sets children of the current element
	 * @param children
	 */
	public void setChildren(Set<PolicyData> children) {
		this.children = children;
	}

	public void addProperty(String key, String value){
		List<String> list = null;
		if(properties.containsKey(key)){
			list = properties.get(key);
		}else{
			list = new ArrayList<String>();
			properties.put(key, list);
		}
		list.add(value);
	}

	public Map<String, List<String>> getProperties(){
		return properties;
	}


	@Override
	public String toString() {
		return "id: " + id + ", class: " + className + ", value: " + value + ", is instance: " + instance;
	}
}
