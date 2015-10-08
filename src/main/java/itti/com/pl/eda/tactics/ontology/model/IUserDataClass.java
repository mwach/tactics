package itti.com.pl.eda.tactics.ontology.model;

import java.io.Serializable;
import java.util.List;

import itti.com.pl.eda.tactics.policy.impl.PolicyElement;

/**
 * interface of ontology class
 * this interface implementations are used in policy generation process
 * @author marcin
 *
 */
public interface IUserDataClass extends Serializable{

	/**
	 * returns PolicyElements that class belongs to
	 * @return
	 */
	public PolicyElement getBranchElement();

	/**
	 * returns instance class name like 'application', 'localization'
	 * @return
	 */
	public String getClassName();

	/**
	 * returns a list of possible instances (eg values for specified variable)
	 * @return
	 */
	public List<String> getInstances();

	/**
	 * returns a list of subclasses
	 * @return
	 */
	public List<IUserDataClass> getChildren();

	/**
	 * returns a list of subclasses in form of string list
	 * @return
	 */
	public List<String> getChildrenList();

	/**
	 * returns a list of all subclasses in form of cascaded string list
	 * @return
	 */
	public List<String> getAllChildrenCascadeList();

	/**
	 * returns if current element is abstract (root)
	 * @return
	 */
	public boolean isAbstract();

	/**
	 * recurrence to find a child with specified name
	 * @param className
	 * @return
	 */
	public IUserDataClass getSubchild(String className);
}
