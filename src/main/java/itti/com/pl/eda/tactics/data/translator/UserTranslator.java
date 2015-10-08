package itti.com.pl.eda.tactics.data.translator;

import itti.com.pl.eda.tactics.operator.User;

/**
 * translates user object between client and server representations
 * @author marcin
 *
 */
public class UserTranslator {

	/**
	 * translates server-side user object into client representation
	 * @param serverUser
	 * @return user object 
	 */
	public static User getOperatorUser(itti.com.pl.eda.tactics.data.model.User serverUser){

		if(serverUser != null){

			User user = new User();

			user.setId(serverUser.getId());
			user.setName(serverUser.getName());
			user.setLocations(
					LocationTranslator.getLocations(serverUser.getLocations()));

			return user;

		}else{
			return null;
		}
	}
}
