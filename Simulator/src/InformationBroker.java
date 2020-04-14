import java.io.Serializable;
import java.net.Inet4Address;

public class InformationBroker implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Inet4Address ipBroker;
	private int port;
	private int idBroker;
	
	public InformationBroker(Inet4Address ipBroker, int port, int idBroker) {
		this.ipBroker = ipBroker;
		this.port = port;
		this.idBroker = idBroker;
	}
	
	public Inet4Address getIpBroker() {
		return ipBroker;
	}
	public void setIpBroker(Inet4Address ipBroker) {
		this.ipBroker = ipBroker;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getIdBroker() {
		return idBroker;
	}
	public void setIdBroker(int idBroker) {
		this.idBroker = idBroker;
	}
}
