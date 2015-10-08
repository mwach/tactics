package itti.com.pl.eda.tactics.policy;

/**
 * an interface for classes containing qos parameters
 * @author marcin
 *
 */
public interface IQoSLevel extends ILReservationInterface{

	/**
	 * list of parameters defined as 'qos parameters'
	 */
	public static final PolicyTripleVariable allowedItems[] = new PolicyTripleVariable[]{
		PolicyTripleVariable.Bandwidth,
		PolicyTripleVariable.Jitter,
		PolicyTripleVariable.Delay,
		PolicyTripleVariable.Loss,
	};

	/**
	 * sets id
	 * @param id
	 */
	public void setId(long id);

	/**
	 * returns value of the id
	 * @return id
	 */
	public long getId();


	/**
	 * sets value of the bandwidth
	 * @param bandwidth
	 */
    public void setBandwidth(int bandwidth);

	/**
	 * sets value of the jitter
	 * @param jitter
	 */
    public void setJitter(int i);

	/**
	 * sets value of the delay
	 * @param delay
	 */
    public void setDelay(int i);

	/**
	 * sets value of the loss
	 * @param loss
	 */
    public void setLoss(int i);

}
