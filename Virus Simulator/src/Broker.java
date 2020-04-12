import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class Broker {
	
	private ArrayList<Pais> paises;
	private ArrayList<String> brokers;
	private Inet4Address ip;
	private DataInputStream input;
	private DataOutputStream output;
	private ServerSocket socketBroker;
	private Socket clientSocket;
	private int port;
	
	public Broker(int pPort) {
		try {
			this.port = pPort;
			this.paises = new ArrayList<Pais>();
			this.brokers = new ArrayList<String>();
			this.clientSocket = new Socket("127.0.0.1", 5000);
			this.ip = (Inet4Address) Inet4Address.getLocalHost();
			try {
				this.output = new DataOutputStream(this.clientSocket.getOutputStream());
				this.input = new DataInputStream(this.clientSocket.getInputStream());
				this.output.writeUTF("Broker asking for confirmation...");
				String msgReceived = this.input.readUTF();
				System.out.println("Message received from Master Broker: " + msgReceived);
				while(true) {
					String msgRequest = this.input.readUTF();
					if(!msgRequest.equalsIgnoreCase("Master Broker finished..." )) {
						System.out.println("Message received from master broker: " + msgRequest);
						if(!this.getIP().getHostAddress().equals(msgRequest)) {
							this.addBroker(msgRequest);
							System.out.println("Broker added another broker with IP: " + msgRequest);
						}
					} else {
						System.out.println("Master Broker finished sending brokers...");
						System.out.println("Creating Broker Server...");
						this.clientSocket.close();
						createClientBroker();
						createBrokerServer();
					}
				}
			} catch (IOException e) {
				System.out.println("Error createStreams - IOExeption");
			}
		} catch (IOException e) {
			System.out.println( "Error constructorBroker - IOException" + e.getMessage() );
		}
	}
		
	public void createClientBroker() {
		try {
			System.out.println("Broker client created...");
			Scanner scn = new Scanner(System.in);
			System.out.println("Enter brokers IP");
			String ip = scn.nextLine();
			System.out.println("Enter brokers port");
			int port = scn.nextInt();
			scn.close();
			clientSocket = new Socket(ip, port);
			ConnectionBrokerClient connection = new ConnectionBrokerClient(clientSocket);
			Thread t = new Thread(connection);
			t.start();
		} catch (UnknownHostException e) {
			System.out.println("Error createClientBroker - UnknownHostException");
		} catch (IOException e) {
			System.out.println("Error createClientBroker - IOException");
		}
	}
	
	public void createBrokerServer() {
		try {
			System.out.println("Broker Server created...");
			this.socketBroker = new ServerSocket(this.port);
			System.out.println("Broker Server initialized...");
			while(true) {
				System.out.println("Waiting for message from other brokers...");
				this.clientSocket = this.socketBroker.accept();
				System.out.println("Broker accepted: " + this.clientSocket);
				ConnectionBrokerServer connection = new ConnectionBrokerServer(this.clientSocket, this);	
				Thread t = new Thread(connection);
				t.start();
			}
		} catch (IOException e) {
			System.out.println("Error createBrokerServer - IOException");
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
		Scanner s = new Scanner(System.in);
		System.out.println("Port");
		int port = s.nextInt();
		s.close();
		new Broker(port);
	}

}
