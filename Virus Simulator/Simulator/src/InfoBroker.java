import java.io.Serializable;
import java.net.Inet4Address;

public class InfoBroker implements Serializable{

	private static final long serialVersionUID = 1L;

	private Inet4Address ipBroker;
	private int portServer;
	private int codigoBroker;

	public InfoBroker(Inet4Address aIp, int apuerto,int aCodigo) {
		this.ipBroker = aIp;
		this.portServer = apuerto;
		this.codigoBroker = aCodigo;
	}

	public Inet4Address getIpBroker() {
		return ipBroker;
	}

	public void setIpBroker(Inet4Address ipBroker) {
		this.ipBroker = ipBroker;
	}

	public int getPortServer() {
		return portServer;
	}

	public void setPortServer(int portServer) {
		this.portServer = portServer;
	}

	public int getCodigoBroker() {
		return codigoBroker;
	}

	public void setCodigoBroker(int codigoBroker) {
		this.codigoBroker = codigoBroker;
	}


}