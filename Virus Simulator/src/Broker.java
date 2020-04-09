import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Broker {
	
	private ArrayList<Pais> paises;
	private ArrayList<String> brokers;
	private Inet4Address ip;
	private Socket socket;
	private DataOutputStream output;
	private DataInputStream input;
	
	public Broker() {
		try {
			this.paises = new ArrayList<Pais>();
			this.brokers = new ArrayList<String>();
			this.socket = new Socket("127.0.0.1", 5000);
			this.output = new DataOutputStream(this.socket.getOutputStream());
			this.input = new DataInputStream(this.socket.getInputStream());
			this.ip = (Inet4Address) Inet4Address.getLocalHost();
			System.out.println("Broker initialized...");
			new ConnectionBroker(this.socket, this);
			
				
		} catch (IOException e) {
			System.out.println( "Error - IOException" + e.getMessage() );
		}
	}
	
	public Inet4Address getIP() {
		return this.ip;
	}
	
	public ArrayList<Pais> getPaises() {
		return this.paises;
	}
	
	public ArrayList<String> getBrokers() {
		return this.brokers;
	}
	
	public void addPais(Pais pais) {
		this.paises.add(pais);
	}
	
	public void addBroker(String broker) {
		this.brokers.add(broker);
	}
	
	

	public static void main(String[] args) {
		new Broker();
	}

}
