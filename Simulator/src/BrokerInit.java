import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class BrokerInit {
	
	private ArrayList<InformationBroker> brokers;
	private ArrayList<Pais> paises;
	private int brokerCount; 
	public static final int MIN = 2;
	public static final int MASTER_PORT = 5000;
	private Socket socket;
	private ServerSocket server;
	
	public BrokerInit() {
		try {
			this.brokerCount = 0;
			this.brokers = new ArrayList<InformationBroker>();
			this.paises = new ArrayList<Pais>();
			this.leerPaises();
			this.server = new ServerSocket(MASTER_PORT);
			System.out.println("Master Broker init...");
			while(true) {
				System.out.println("Waiting for broker to connect...");
				this.socket = this.server.accept();
				new ConnectionInit(this.socket, this);	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	public void sendPaises(InformationBroker dest) {
		ArrayList<Pais> aux = new ArrayList<Pais>();
		for(Pais actual: this.paises) {
			if(actual.getIdBroker() == dest.getIdBroker()) {
				aux.add(actual);
			}
		}
		initClient(dest, 2, aux);
	}
	
	public void sendIps(InformationBroker dest) {
		initClient(dest, 1, this.getBrokers());
	}
	
	public void leerPaises() throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(new File("./archivos/paises.txt")));
		String line = br.readLine();

		while (line != null) {
			String[] sep = line.split(";");
			Pais nuevo = new Pais(Integer.parseInt(sep[0]), sep[1], Double.parseDouble(sep[2]), Double.parseDouble(sep[3]), Double.parseDouble(sep[4]), Double.parseDouble(sep[5]), Integer.parseInt(sep[6]));
			this.paises.add(nuevo);
			line = br.readLine();
		}
		br.close();

//		br = new BufferedReader(new FileReader(new File("./archivos/vecinos.txt")));
//		line = br.readLine();
//
//		while (line != null) {
//			String[] sep = line.split(";");
//			Pais duenio = getPais(sep[0]);
//			Pais ingresar = getPais(sep[1]);
//			duenio.addNeigh(ingresar);
//			line = br.readLine();
//		}
//		br.close();
		
	}
	
	public Pais getPais(String nombre) {
		for (Pais actual : this.paises) {
			if (actual.getNombre().equalsIgnoreCase(nombre)) {
				return actual;
			}
		}
		return null;
	}
	
	
	public void addBroker(InformationBroker broker) {
		this.brokers.add(broker);
		System.out.println("New broker added to list");
		this.brokerCount++;
	}
	
	public int getBrokerCount() {
		return this.brokerCount;
	}
	
	public ArrayList<InformationBroker> getBrokers() {
		return this.brokers;
	}
	
	public ArrayList<Pais> getPaises(){
		return this.paises;
	}

	public static void main(String[] args) {
		new BrokerInit();
	}
}
