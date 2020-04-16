import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class BrokerInit {

	private ArrayList<InfoBroker> brokers;
	private ArrayList<Pais> paises;
	public static final int MIN = 3;
	public static final int MASTER_PORT = 5000;
	private Socket socket;
	private ServerSocket server;

	public BrokerInit() {
		try {
			brokers = new ArrayList<InfoBroker>();
			paises = new ArrayList<Pais>();
			this.server = new ServerSocket(BrokerInit.MASTER_PORT);
			System.out.println("Master Broker inició...");
			leerPaises();
			while (true) {
				System.out.println("Esperando mensajes de brokers...");
				this.socket = this.server.accept();
				System.out.println("Broker aceptadó: " + this.socket);
				ConnectionBrokerInit t = new ConnectionBrokerInit(this);
				t.reply(socket);
			}

		} catch (IOException e) {
			System.out.println("Error IOException in BrokerInit() " + e.getMessage());
		}
	}

	public ArrayList<InfoBroker> getBrokers() {
		return this.brokers;
	}
	
	public void agregarBroker(InfoBroker nuevoBroker) {
		this.brokers.add(nuevoBroker);
	}

	public static int getMin() {
		return MIN;
	}

	public void leerPaises() throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(new File("./archivos/paises.txt")));
		String line = br.readLine();

		while (line != null) {
			String[] sep = line.split(";");
			Pais nuevo = new Pais(Integer.parseInt(sep[0]), sep[1], Double.parseDouble(sep[2]),
					Double.parseDouble(sep[3]), Double.parseDouble(sep[4]), Double.parseDouble(sep[5]),
					Integer.parseInt(sep[6]),Double.parseDouble(sep[7]),Double.parseDouble(sep[8]),Double.parseDouble(sep[9])
					,sep[10]);
			this.paises.add(nuevo);
			line = br.readLine();
		}
		br.close();

		br = new BufferedReader(new FileReader(new File("./archivos/vecinos.txt")));
		line = br.readLine();

		while (line != null) {
			String[] sep = line.split(";");
			Pais duenio = getPais(sep[0]);
			Pais ingresar = getPais(sep[1]);
			duenio.addNeigh(ingresar);
			line = br.readLine();
		}
		br.close();
	}

	public Pais getPais(String nombre) {
		for (Pais actual: this.paises) {
			if (actual.getNombre().equalsIgnoreCase(nombre)) {
				return actual;
			}
		}
		return null;
	}
	
	public ArrayList<Pais> getPaises() {
		return paises;
	}

	public void setPaises(ArrayList<Pais> paises) {
		this.paises = paises;
	}
	
	/**
	 * Main BrokerInit
	 * @param args
	 */
	public static void main(String[] args) {
		new BrokerInit();
	}

}