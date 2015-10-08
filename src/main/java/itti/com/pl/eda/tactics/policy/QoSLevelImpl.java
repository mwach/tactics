package itti.com.pl.eda.tactics.policy;

public class QoSLevelImpl implements IQoSLevel{

	private static final long serialVersionUID = 1L;

	private long id = -1;

	private int delay = -1;
	private int jitter = -1;
	private int reliability = -1;
	private int bandwidth = -1;
	private int loss = -1;

	public long getId(){
		return id;
	}

	public void setId(long l){
		id = l;
	}

	public int getDelay() {
		return delay;
	}

	public int getJitter() {
		return jitter;
	}

	public int getReliability() {
		return reliability;
	}

	public int getBandwidth() {
		return bandwidth;
	}

	public int getLoss() {
		return loss;
	}

	public void setDelay(int i) {
		delay = i;
	}

	public void setJitter(int i) {
		jitter = i;
	}

	public void setReliability(int i) {
		reliability = i;
	}

	public void setBandwidth(int i) {
		bandwidth = i;
	}

	public void setLoss(int i) {
		loss = i;
	}


	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id: " + id + "\n");
		sb.append("jitter: " + jitter + "\n");
		sb.append("bandwidth: " + bandwidth + "\n");
		sb.append("delay: " + delay + "\n");
		sb.append("reliability: " + reliability + "\n");
		sb.append("loss: " + loss + "\n");
		return sb.toString();
	}
}
