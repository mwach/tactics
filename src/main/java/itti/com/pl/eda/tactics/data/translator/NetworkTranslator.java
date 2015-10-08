package itti.com.pl.eda.tactics.data.translator;

import itti.com.pl.eda.tactics.data.model.Network;

public class NetworkTranslator {

	public static Network getNetwork(itti.com.pl.eda.tactics.operator.Network networkObj) {

		if(networkObj != null){
			Network network = new Network();
			network.setId(networkObj.getId());
			network.setName(networkObj.getName());

			network.setBandwidth(networkObj.getBandwidth());
			network.setJitter(networkObj.getJitter());
			network.setLoss(networkObj.getLoss());
			network.setDelay(networkObj.getDelay());

			network.setCondBandwidth(networkObj.getCondBandwidth());
			network.setCondJitter(networkObj.getCondJitter());
			network.setCondLoss(networkObj.getCondLoss());
			network.setCondDelay(networkObj.getCondDelay());

			return network;
		}
		return null;
	}

	public static itti.com.pl.eda.tactics.operator.Network getNetwork(Network networkObj) {

		if(networkObj != null){
			itti.com.pl.eda.tactics.operator.Network network = new itti.com.pl.eda.tactics.operator.Network();
			network.setId(networkObj.getId());
			network.setName(networkObj.getName());

			network.setBandwidth(networkObj.getCondBandwidth(), networkObj.getBandwidth());
			network.setJitter(networkObj.getCondJitter(), networkObj.getJitter());
			network.setLoss(networkObj.getCondLoss(), networkObj.getLoss());
			network.setDelay(networkObj.getCondDelay(), networkObj.getDelay());

			return network;
		}
		return null;
	}
}
