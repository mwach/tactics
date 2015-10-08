package itti.com.pl.eda.tactics.ontology.model;

import java.util.ArrayList;
import java.util.List;

import itti.com.pl.eda.tactics.policy.impl.PolicyElement;

/**
 * stores all possible instances of policy element
 * used in policy generation process
 * @author marcin
 *
 */
public class UserDataImpl implements IUserDataClass{

	private static final long serialVersionUID = 1L;

	//element name eg contitionVariable
	private PolicyElement element = null;

	//class name eg application, localization
	private String className = null;

	//list of all subclasses
	private List<IUserDataClass> children = new ArrayList<IUserDataClass>();

	//list of instances (possible values)
	private List<String> instances = new ArrayList<String>();

	private boolean _abstract = false;


	/**
	 * default constructor
	 * @param element policy element
	 * @param className ontology class name
	 */
	public UserDataImpl(PolicyElement element, String className){
		this.element = element;
		this.className = className;
		if(className == null){
			_abstract = true;
		}
	}


	/**
	 * adds a instance to the object
	 * @param instance
	 */
	public void addInstance(String instance){
		this.instances.add(instance);
	}

	/**
	 * adds a child to the object
	 * @param child
	 */
	public void addChild(IUserDataClass child){
		this.children.add(child);
	}

	public PolicyElement getBranchElement() {
		return element;
	}

	public List<IUserDataClass> getChildren() {
		return children;
	}

	public boolean isAbstract(){
		return _abstract;
	}

	public List<String> getChildrenList() {

		List<String> retList = new ArrayList<String>();
		for (IUserDataClass child : children) {
			retList.add(child.getClassName());
		}
		return retList;
	}

	public String getClassName() {
		return className;
	}

	public List<String> getInstances() {
		return instances;
	}

	public List<String> getAllChildrenCascadeList() {

		List<String> rootLevelchildren = getChildrenList();
		for (IUserDataClass  child : children) {
			List<String> subChildren = child.getAllChildrenCascadeList();
			rootLevelchildren.addAll(subChildren);
		}

		return rootLevelchildren;
	}

	public IUserDataClass getSubchild(String className){
		if(children != null){
			for (IUserDataClass child : children) {
				if(child.getClassName() != null && child.getClassName().equalsIgnoreCase(className)){
					return child;
				}else{
					IUserDataClass grandChild = child.getSubchild(className);
					if(grandChild != null){
						return grandChild;
					}
				}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return (element != null ? element.name() : "") + ", " + className;
	}
}
