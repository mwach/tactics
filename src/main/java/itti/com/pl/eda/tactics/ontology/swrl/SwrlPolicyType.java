package itti.com.pl.eda.tactics.ontology.swrl;

import itti.com.pl.eda.tactics.exception.PolicyTypeException;
import itti.com.pl.eda.tactics.exception.PolicyTypeException.PolicyTypeExceptionType;
import itti.com.pl.eda.tactics.utils.StringUtils;

/**
 * class keeps info about single policy type
 * policy types are loaded during pold server startup from policy.types.definition.xml config file
 * @author marcin
 *
 */
public class SwrlPolicyType{

	private String name;
	private String swrlQuery;
	private String description;


	/**
	 * default constructor
	 * @param pName name of the policy
	 * @param pSwrlQuery qury, which specifies policy conditions
	 * @param pDescription user-readable simple policy description
	 * @throws PolicyTypeException
	 */
	public SwrlPolicyType(String pName, String pSwrlQuery, String pDescription) throws PolicyTypeException{

		if(StringUtils.isNullOrEmpty(pName)){
			throw new PolicyTypeException(PolicyTypeExceptionType.EmptyName);
		}else{
			name = pName;
		}

		if(StringUtils.isNullOrEmpty(pSwrlQuery)){
			throw new PolicyTypeException(PolicyTypeExceptionType.EmptySwrlRule);
		}else{
			swrlQuery = pSwrlQuery;
		}

		description = pDescription;
	}


	/**
	 * returns policy name
	 * @return name
	 */
	public String getName() {
		return name;
	}


	/**
	 * returns query which defines policy
	 * @return query which defines policy
	 */
	public String getSwrlQuery() {
		return swrlQuery;
	}


	/**
	 * returns policy description
	 * @return policy description
	 */
	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return "Name: " + name + ", query: " + swrlQuery;
	}
}
