package eu.netqos.apm.policy;

public class IntermediateCommonServicePolicy extends IntermediatePolicy{

	private static final long serialVersionUID = 1L;

	//high level specifications
	private String application;	//must be mapped, required by Pador 
								//operational policy
	private String businessQos; //Hig or low level,  required by 
								//Pedro operational policy

	//intermediate level values
	private String bandwidth;	//*** structure defined in D2.7, can be 
								//required by all of the partners
	private String delay;		//*** structure defined in D2.7, can be required 
								//by all of the partners
	private String jitter;		//*** structure defined in D2.7, can be 
								//required by all of the partners
	private String synchro;		// *** structure defined in D2.7, can be 
								//required by all of the partners
	private String loss;		// *** structure defined in D2.7, can be required 
								//by all of the partners
	private String ipAddress;


	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	public String getBusinessQos() {
		return businessQos;
	}
	public void setBusinessQos(String businessQos) {
		this.businessQos = businessQos;
	}
	public String getBandwidth() {
		return bandwidth;
	}
	public void setBandwidth(String bandwidth) {
		this.bandwidth = bandwidth;
	}
	public String getDelay() {
		return delay;
	}
	public void setDelay(String delay) {
		this.delay = delay;
	}
	public String getJitter() {
		return jitter;
	}
	public void setJitter(String jitter) {
		this.jitter = jitter;
	}
	public String getSynchro() {
		return synchro;
	}
	public void setSynchro(String synchro) {
		this.synchro = synchro;
	}
	public String getLoss() {
		return loss;
	}
	public void setLoss(String loss) {
		this.loss = loss;
	}

	public void setIpAddress(String ipAddress){
		this.ipAddress = ipAddress;
	}
	public String getIpAddress(){
		return ipAddress;
	}


	@Override
	public String toString() {
		return "Actor: " + getActor() + ", application: " + getApplication();
	}
}
