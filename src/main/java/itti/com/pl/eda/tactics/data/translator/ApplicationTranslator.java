package itti.com.pl.eda.tactics.data.translator;

import itti.com.pl.eda.tactics.operator.Application;

public class ApplicationTranslator {

	public static Application getApplication(Application application){

		Application app = new Application();

		app.setId(application.getId());
		app.setName(application.getName());
		app.setType(application.getType());
		app.setBandwidth(application.getBandwidth());
		app.setDelay(application.getDelay());
		app.setJitter(application.getJitter());
		app.setLoss(application.getLoss());

		app.setCondBandwidth(application.getCondBandwidth());
		app.setCondDelay(application.getCondDelay());
		app.setCondJitter(application.getCondJitter());
		app.setCondLoss(application.getCondLoss());

		return app;
	}
}
