package itti.com.pl.eda.tactics.policy;

import eu.netqos.utils.policies.ConnectionId;

/**
 * interface of an intermediate policy (generic part)
 * @author marcin
 */
public interface ILPolicy{

    /**
     * returns the id of the policy
     * @return int 
     */
    public long getId();

    /**
     * returns the weight of the policy
     * @return int
     */
    public int getWeight();

    /**
     * returns the policy description
     * @return String
     */
    public String getDescription();

    /**
     * returns true if policy is active and false otherwise 
     * @return  boolean 
     */
    public boolean isActive();

    /**
     * sets the policy to active state
     * @param active
     */
    public void setActive(boolean pActive);


    /**
     * returns policy qos level
     * @return
     */
    public IQoSLevel getQoSLevel();


    /**
     * returns IP address
     * @return
     */
    public String getIpAddress();

    /**
     * returns network name
     * @return
     */
    public String getNetwork();


    /**
     * returns policy conditions
     * @return conditions
     */
    public PolicyTriple getConditions();


    /**
     * returns policy actions
     * @return actions
     */
    public PolicyTriple getActions();


    /**
     * returns policy time conditions
     * @return time conditions
     */
    public TimeCondition getTimeConditions();


    /**
     * returns connectionId object
     * @return connectionId
     */
    public ConnectionId getConnectionId();

    /**
     * returns author name
     * @return authorName
     */
    public String getAuthorName();

    /**
     * returns author id
     * @return authorid
     */
    public long getAuthorId();
}
