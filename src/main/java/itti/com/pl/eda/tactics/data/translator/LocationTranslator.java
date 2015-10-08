package itti.com.pl.eda.tactics.data.translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import itti.com.pl.eda.tactics.data.model.Location;
import itti.com.pl.eda.tactics.data.model.Network;

/**
 * translates pold server location object into client-side location bean object
 * @author marcin
 *
 */
public class LocationTranslator {

	/**
	 * translates client-side location object into server one
	 * @param clientLocation 
	 * @param network must be setted individually
	 * @return serverLocation
	 */
	public static Location getLocation(itti.com.pl.eda.tactics.operator.Location clientLocation, Map<String, Network> networks){

		if(clientLocation != null){

			Location serverLocation = new Location();
			serverLocation.setId(clientLocation.getId());
			serverLocation.setLocationName(clientLocation.getLocationName());
			serverLocation.setQosClass(clientLocation.getQosClass());
			serverLocation.setNetworks(networks);

			return serverLocation;

		}else{
			return null;
		}
	}


	/**
	 * translates server-side location object into client one
	 * @param serverLocation 
	 * @return clientLocation
	 */
	public static itti.com.pl.eda.tactics.operator.Location getLocation(Location serverLocation){

		if(serverLocation != null){

			itti.com.pl.eda.tactics.operator.Location clientLocation = new itti.com.pl.eda.tactics.operator.Location();
			clientLocation.setId(serverLocation.getId());
			clientLocation.setLocationName(serverLocation.getLocationName());
			clientLocation.setQosClass(serverLocation.getQosClass());

			if(serverLocation.getNetworks() != null){
				Map<String, String> networks = new HashMap<String, String>();
				for (String ipAddress : serverLocation.getNetworks().keySet()) {
					Network net = serverLocation.getNetworks().get(ipAddress);
					networks.put(ipAddress, net.getName());
				}
				clientLocation.setNetworks(networks);
			}
			return clientLocation;
		}else{
			return null;
		}
	}

	/**
	 * translates the whole set of server-side locations into client-side list
	 * @param modelLocations server-side set
	 * @return client-side list
	 */
	public static List<itti.com.pl.eda.tactics.operator.Location> getLocations(
			Set<Location> modelLocations) {

		if(modelLocations == null){
			return null;

		}else{
			List<itti.com.pl.eda.tactics.operator.Location> locations = new ArrayList<itti.com.pl.eda.tactics.operator.Location>();

			for (Location location : modelLocations) {
				itti.com.pl.eda.tactics.operator.Location bean = getLocation(location);
				if(bean != null){
					locations.add(bean);
				}
			}
			return locations;
		}
	}
}
