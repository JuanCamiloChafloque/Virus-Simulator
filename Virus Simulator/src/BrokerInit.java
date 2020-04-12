import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class BrokerInit {
	
	private ArrayList<ConnectionInit> brokers;
	private int brokerCount; 
	public static final int MIN = 2;
	public static final int PORT = 5000;
	private Socket socket;
	private ServerSocket server;
	
	public BrokerInit() {
		try {
			brokerCount = 0;
			brokers = new ArrayList<ConnectionInit>();
			this.server = new ServerSocket(PORT);
			System.out.println("Master Broker init...");
			while(true) {
				System.out.println("Waiting for broker to connect...");
				this.socket = this.server.accept();
				System.out.println("Broker accepted: " + this.socket);
				System.out.println("Creating new thread for broker " + (this.brokerCount + 1));
				ConnectionInit connection = new ConnectionInit(this.socket, this);	
				Thread t = new Thread(connection);
				this.addBroker(connection);
				t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addBroker(ConnectionInit broker) {
		this.brokers.add(broker);
		System.out.println("New broker added to list");
		this.brokerCount++;
	}
	
	public int getBrokerCount() {
		return this.brokerCount;
	}
	
	public ArrayList<ConnectionInit> getBrokers() {
		return this.brokers;
	}

	public static void main(String[] args) {
		new BrokerInit();
	}
}
