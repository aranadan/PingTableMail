package pingTableMail;

import javafx.beans.property.SimpleStringProperty;


public class IpHost {
	private final SimpleStringProperty ip = new SimpleStringProperty("");
	private final SimpleStringProperty hostName = new SimpleStringProperty("");
	private String response ;
	private String time ;
	private String status = "UP";

	public IpHost() {}

	public IpHost(String ip) {
		this.ip.set(ip);
	}

	public IpHost(String ip, String name) {
		this.ip.set(ip);
		this.hostName.set(name);
	}

	public String getIp() {
		return ip.get();
	}

	public String getResponse() {return response;}

	public void setIp(String ip) {
		this.ip.set(ip);
	}

	public void setResponse(String response) {this.response = response;}

	public String getTime() {
		return time;
	}

	void setTime(String time) {
		this.time = time;
	}

	String getStatus() {
		return status;
	}

	void setStatus(String status) {this.status=status;}

	public String getHostName() {
		return hostName.get();
	}

	public void setHostName(String hostName) {
		this.hostName.set(hostName);
	}

}
