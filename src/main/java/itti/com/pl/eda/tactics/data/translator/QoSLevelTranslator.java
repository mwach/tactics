package itti.com.pl.eda.tactics.data.translator;

import itti.com.pl.eda.tactics.data.model.QoSLevel;
import itti.com.pl.eda.tactics.policy.IQoSLevel;
import itti.com.pl.eda.tactics.policy.QoSLevelImpl;

/**
 * translates server-side qos level object into client-side ones and vice-versa
 * @author marcin
 *
 */
public class QoSLevelTranslator {

	/**
	 * translates server-side qos level object into client-side ones and vice-versa
	 * @param oldQoSLevel original qos level
	 * @return opposite-side qos level
	 */
	public static IQoSLevel getQoSLevel(IQoSLevel oldQoSLevel){

		if(oldQoSLevel != null){

			if(oldQoSLevel instanceof QoSLevelImpl){

				IQoSLevel serverQoSLevel = new QoSLevel();
				serverQoSLevel.setId(oldQoSLevel.getId());

				serverQoSLevel.setBandwidth(oldQoSLevel.getBandwidth());
				serverQoSLevel.setDelay(oldQoSLevel.getDelay());
				serverQoSLevel.setJitter(oldQoSLevel.getJitter());
				serverQoSLevel.setLoss(oldQoSLevel.getLoss());

				return serverQoSLevel;

			}else{

				IQoSLevel clientQoSLevel = new QoSLevelImpl();
				clientQoSLevel.setId(oldQoSLevel.getId());

				clientQoSLevel.setBandwidth(oldQoSLevel.getBandwidth());
				clientQoSLevel.setDelay(oldQoSLevel.getDelay());
				clientQoSLevel.setJitter(oldQoSLevel.getJitter());
				clientQoSLevel.setLoss(oldQoSLevel.getLoss());

				return clientQoSLevel;
			}

		}else{
			return null;
		}
	}
}
