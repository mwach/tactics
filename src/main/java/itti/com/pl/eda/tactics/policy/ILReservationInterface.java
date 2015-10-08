package itti.com.pl.eda.tactics.policy;

import java.io.Serializable;

/**
 * interface for reservation policies
 * @author marcin
 *
 */
public interface ILReservationInterface extends Serializable{

	/**
	 * returns value of the bandwidth
	 * @return bandwidth
	 */
    public int getBandwidth();

	/**
	 * returns value of the jitter
	 * @return jitter
	 */
    public int getJitter();

	/**
	 * returns value of the delay
	 * @return delay
	 */
    public int getDelay();

	/**
	 * returns value of the loss
	 * @return loss
	 */
    public int getLoss();
}
