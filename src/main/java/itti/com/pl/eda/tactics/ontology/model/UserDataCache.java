package itti.com.pl.eda.tactics.ontology.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import itti.com.pl.eda.tactics.policy.impl.PolicyElement;

/**
 * object used to keep all information related to the specified user
 * contains values, which can be used to prepare valid policies by the user
 * @author marcin
 *
 */
public class UserDataCache implements Serializable{

	private static final long serialVersionUID = 1L;

	private Map<PolicyElement, IUserDataClass> items = new HashMap<PolicyElement, IUserDataClass>();


	/**
	 * returns a list of instances of the specified policy element and ontology class name
	 * @param elementType policy element
	 * @param className ontology class name
	 * @return list of instances or null if there are no available instances
	 */
	public List<String> getInstancesList(PolicyElement elementType, String className){

		if(className == null && items.containsKey(elementType)){
			IUserDataClass item = items.get(elementType);
			if(item.isAbstract()){
				return item.getAllChildrenCascadeList();
			}else{
				return item.getInstances();
			}
		}else if (className != null){

			if(elementType == PolicyElement.ActionValue){
				elementType = PolicyElement.ActionVariable;
			}else if(elementType == PolicyElement.ConditionValue){
				elementType = PolicyElement.ConditionVariable;
			}

			IUserDataClass rootClass = items.get(elementType);
			IUserDataClass child = rootClass.getSubchild(className);
			if(child != null){
				return child.getInstances();
			}
		}
		return null;
	}

	/**
	 * adds a new item to the object
	 * @param item user related object with data
	 */
	public void addItem(IUserDataClass item){
		items.put(item.getBranchElement(), item);
	}



}