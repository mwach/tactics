package eu.netqos.apm.policy;

import java.io.Serializable;

public class IntermediatePolicy implements Serializable{

	private static final long serialVersionUID = 1L;

	private int ident;		//policy id must be mapped
	private String actor;	//can be extended with actor IP 
							//address, therefore  the mapping of  actor address can be considered
	private String description;	// policy description must be mapped
	private String sla;			//SLA reference - used to obtain for instance the IP address of the actor , as agreed with ISP operator (required by 
								// operational policy)
	private String context; // don't care now about it : context can  
							//be used for some environment description, but also for the type of policy (our Internet Draft)
	public int getIdent() {
		return ident;
	}
	public void setIdent(int ident) {
		this.ident = ident;
	}
	public String getActor() {
		return actor;
	}
	public void setActor(String actor) {
		this.actor = actor;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSla() {
		return sla;
	}
	public void setSla(String sla) {
		this.sla = sla;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
}
