import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Broker extends Thread implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int nombre;
	private ArrayList<Pais> paises;
	private ArrayList<InformationBroker> brokers;
	private Inet4Address ip;
	private int port;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket socketBroker;
	private Socket clientSocket;
	private Socket listeningSocket;

	public Broker() {
		try {
			this.paises = new ArrayList<Pais>();
			this.brokers = new ArrayList<InformationBroker>();
			this.port = 1234;
			this.clientSocket = new Socket("127.0.0.1", 5000);
			this.ip = (Inet4Address) Inet4Address.getLocalHost();
			try {
				this.output = new ObjectOutputStream(this.clientSocket.getOutputStream());
				this.input = new ObjectInputStream(this.clientSocket.getInputStream());
				System.out.println("Sending information to broker master...");
				this.output.writeObject(this.ip);
				this.output.writeObject(this.port);
				String msgReceived = this.input.readObject().toString();
				System.out.println("Id assigned from broker master: " + msgReceived);
				this.nombre = Integer.parseInt(msgReceived);
				System.out.println("Broker with id " + this.nombre + "and port: " + this.port + " init server...");
				this.socketBroker = new ServerSocket(this.port);
				while(true) {
					System.out.println("Waiting message from master/broker/pais...");
					this.listeningSocket = this.socketBroker.accept();
					System.out.println("Message accepted...");
					new ConnectionBroker(listeningSocket, this);
				}
				
			} catch (IOException e) {
				System.out.println("Error constructorBroker - IOException" + e.getMessage());
			} catch (ClassNotFoundException e) {
				System.out.println("Error constructorBroker - ClassNotFoundException" + e.getMessage());
			}
		} catch (IOException e) {
			System.out.println( "Error constructorBroker - IOException" + e.getMessage() );
		}
	}
	
	public void run() {


	}
	
	public void initClient(InformationBroker dest, int type, Object content) {
		
		Message mensaje = null;
		ObjectOutputStream output = null;
		try {
			Socket sock = new Socket(dest.getIpBroker().getHostName(), dest.getPort());
			mensaje = new Message(dest.getIdBroker(), type, content);
			output = new ObjectOutputStream(sock.getOutputStream());
			output.writeObject(mensaje);
			sock.close();
			
		} catch(IOException e) {
			System.out.println("Error IOExceiption - initClientBroker");
		}
	}
	
	public int getNombre() {
		return this.nombre;
	}

	public Inet4Address getIP() {
		return this.ip;
	}
	
	public int getPort() {
		return this.port;
	}

	public ArrayList<Pais> getPaises() {
		return this.paises;
	}

	public ArrayList<InformationBroker> getBrokers() {
		return this.brokers;
	}

	public void addPais(Pais pais) {
		this.paises.add(pais);
	}

	public void addBroker(InformationBroker ip) {
		this.brokers.add(ip);
	}
	
	public void setNombre(int nombre) {
		this.nombre = nombre;
	}
	
	public void setPort(int port) {
		this.port = port;
	}

	public static void main(String[] args) {
		new Broker();
	}

}
