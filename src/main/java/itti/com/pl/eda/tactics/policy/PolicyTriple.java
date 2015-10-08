package itti.com.pl.eda.tactics.policy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import itti.com.pl.eda.tactics.exception.PolicySectionException;
import itti.com.pl.eda.tactics.exception.PolicySectionException.PolicySectionExceptionType;
import itti.com.pl.eda.tactics.utils.StringUtils;


/**
 * internal class used to keep conditions/actions for specified policy
 * each instance of this class keeps one relation
 * instances can be embedded and stored in hierarchical structure
 * @author marcin
 *
 */
public abstract class PolicyTriple implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long id = -1;

	private PolicyTripleVariable variable = null;
	private PolicyTripleOperator operator = null;
	private String value = null;

	private PolicyTriple root = null;

	private List<PolicyTriple> elements = new ArrayList<PolicyTriple>();

	private LogicalOperator logicalOperator = LogicalOperator.And; 


	/**
	 * default non-argument constructor
	 */
	public PolicyTriple(){
	}

	/**
	 * constructor which sets default logical operator
	 * @param defaultOperator logical operator used for current triple
	 */
	public PolicyTriple(LogicalOperator defaultOperator){
		this.logicalOperator = defaultOperator;
	}

	/**
	 * constructor
	 * @param variable variable of the triple
	 * @param operator operator of the triple
	 * @param value value of the triple
	 */
	public PolicyTriple(PolicyTripleVariable variable, PolicyTripleOperator operator, String value){
		this.variable = variable;
		this.operator = operator;
		this.value = value;
	}


	/**
	 * for hibernate usage only
	 * @param id id of the triple
	 */
	@SuppressWarnings("unused")
	private void setId(long id){
		this.id = id;
	}

	/**
	 * returns value of the id
	 * @return id
	 */
	public long getId(){
		return id;
	}

	/**
	 * returns value of the logical operator as LogicalOperator enumeration item
	 * @return logicalOperator
	 */
	public LogicalOperator getLogicalOperatorEnum(){
		return logicalOperator;
	}

	/**
	 * returns value of the logical operator as string
	 * @return logical operator
	 */
	public String getLogicalOperator(){
		return logicalOperator.name();
	}

	/**
	 * for hibernate usage only
	 * @param operator value of the logical operator
	 */
	@SuppressWarnings("unused")
	private void setLogicalOperator(String operator){
		if(operator != null){
			logicalOperator = LogicalOperator.getValue(operator);
		}
	}

	/**
	 * returns value of the triple variable
	 * @return variable
	 */
	public String getVariable() {
		if(variable != null){
			return variable.name();
		}else{
			return null;
		}
	}

	/**
	 * returns value of the triple variable as PolicyTripleVariable item
	 * @return variable
	 */
	public PolicyTripleVariable getVariableEnum() {
		return variable;
	}

	/**
	 * for hibernate usage only
	 * sets variable value
	 * @param variable value of the triple variable
	 * @throws PolicySectionException
	 */
	@SuppressWarnings("unused")
	private void setVariable(String variable) throws PolicySectionException {
		try{
			if(variable != null){
				this.variable = PolicyTripleVariable.valueOf(variable);
			}
		}catch (Exception e) {
			throw new PolicySectionException(PolicySectionExceptionType.InvalidQoSValue, variable);
		}
		checkIntegrity();
	}


	/**
	 * returns value of the triple operator
	 * @return operator
	 */
	public String getOperator() {
		if(operator != null){
			return operator.name();
		}else{
			return null;
		}
	}

	/**
	 * returns value of the triple operator as PolicyTripleOperator item
	 * @return operator
	 */
	public PolicyTripleOperator getOperatorEnum() {
		return operator;
	}


	/**
	 * sets value of the triple operator
	 * @param operator operator
	 */
	public void setOperatorEnum(PolicyTripleOperator operator){
		this.operator = operator;
	}

	/**
	 * for hibernate usage only
	 * sets operator value
	 * @param operator value of the triple operator
	 * @throws PolicySectionException
	 */
	@SuppressWarnings("unused")
	private void setOperator(String operator) throws PolicySectionException {

		if(operator != null){
			this.operator = PolicyTripleOperator.valueOf(operator);
		}
		checkIntegrity();
	}


	/**
	 * returns value of the policy triple value
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * for hibernate usage only
	 * sets triple value
	 * @param value value of the triple value
	 */
	private void setValue(String value){
		this.value = value;
	}

	/**
	 * for hibernate usage only
	 * sets root triple of the current one
	 * @param root value of the triple root
	 */
	private void setRoot(PolicyTriple root){
		this.root = root;
	}

	/**
	 * for hibernate usage only
	 * returns root value of the current one
	 * @return root
	 */
	@SuppressWarnings("unused")
	private PolicyTriple getRoot(){
		return root;
	}

	/**
	 * indicated triple section: action or condition
	 * @return A for actions, C for conditions
	 */
	public abstract char getSection();

	/**
	 * indicated policy triple level: high or intermediate level
	 * @return H for high-level, L for intermediate-level policies
	 */
	public abstract char getPolicyType();

	/**
	 * sets triple section
	 * @param section value A for actions, C for conditions
	 */
	public final void setSection(char section){}

	/**
	 * sets triple type
	 * @param type value H for high-level, L for intermediate-level policies
	 */
	public final void setPolicyType(char type){}

	public void addElement(PolicyTriple element){

		PolicyTriple currElem = getElement(element.getVariableEnum());

		if(currElem != null){
			currElem.setOperatorEnum(element.getOperatorEnum());
			currElem.setValue(element.getValue());

		}else{
			elements.add(element);
			element.setRoot(this);
		}
	}

	/**
	 * returns triple children (embedded triples)
	 * @return tripe children
	 */
	public List<PolicyTriple> getElements(){
		return elements;
	}

	/**
	 * return child specified by its variable value
	 * @param variable value of the expected triple variable
	 * @return matching child, or null if nothing was found
	 */
	public PolicyTriple getElement(PolicyTripleVariable variable){
		if(elements != null){
			for (PolicyTriple element : elements) {
				if(element.getVariableEnum() == variable){
					return element;
				}
			}
		}
		return null;
	}

	/**
	 * returns value of the child triple, which variable matches specified one
	 * @param variable variable to search
	 * @return value of the founded triple, or null if nothing was found
	 */
	public String getElementValue(PolicyTripleVariable variable){
		if(elements != null){
			for (PolicyTriple element : elements) {
				if(element.getVariableEnum() == variable){
					return element.getValue();
				}
			}
		}
		return null;
	}


	/**
	 * returns amount of triple children
	 * @return length
	 */
	public int getElementsLength(){
		return elements == null ? 0 : elements.size();
	}

	/**
	 * for hibernate usage only
	 * @param list sets triple children
	 */
	@SuppressWarnings("unused")
	private void setElements(List<PolicyTriple> list){
		this.elements = list;
	}

	private void checkIntegrity() throws PolicySectionException{

		if((getValue() != null || getVariable() != null || getOperator() != null) &&
				getElementsLength() > 0){
			throw new PolicySectionException(PolicySectionExceptionType.BothTypesSetted, 
					toString());
		}
	}

	/**
	 * checks if given triple matches specified criteria
	 * @param triple examined triple
	 * @return true, if triple matches criteria, false otherwise
	 * @throws PolicySectionException
	 */
	public static boolean matchesQoSCriteria(PolicyTriple triple) throws PolicySectionException {
//TODO: implement - used in criteria like ><= etc...
		return true;
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();

		if(getElementsLength() > 0){
			sb.append("(");
			for (PolicyTriple ps : elements) {
				sb.append(ps.toString());
				sb.append(" " + logicalOperator + " ");
			}
			sb.delete(sb.length() - logicalOperator.name().length() - 2, sb.length());
			sb.append(")");
		}else{
			sb.append(variable + " " + operator + " "+ value);
		}

		return sb.toString();
	}


	@Override
	public boolean equals(Object obj) {

		if((obj == null) || !(obj instanceof PolicyTriple)){
			return false;
		}

		PolicyTriple objPs = (PolicyTriple)obj;

		return
			StringUtils.compareStrings(getVariable(), objPs.getVariable()) && 
			StringUtils.compareStrings(getOperator(), objPs.getOperator()) &&
			StringUtils.compareStrings(getValue(), objPs.getValue()) &&
			(getElements() != null && objPs.getElements() != null && getElements().equals(objPs.getElements())) &&
			getPolicyType() == objPs.getPolicyType() &&
			getSection() == objPs.getSection() &&
			getLogicalOperator() == objPs.getLogicalOperator();
	}

	@Override
	public int hashCode() {
		return 
			((getVariable() == null) ? 0 : getVariable().hashCode()) +
			((getOperator() == null) ? 0 : getOperator().hashCode()) +
			((getValue() == null) ? 0 : getValue().hashCode()) + 
			getPolicyType() + 
			getSection() + 
			getLogicalOperator().hashCode() + 
			getElements().hashCode();
	}
}
