import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class BrokerInit {
	
	private ArrayList<String> brokers;
	private int brokerCount; 
	public static final int MIN = 2;
	private Socket socket;
	private ServerSocket server;
	
	
	public BrokerInit() {
		try {
			brokerCount = 0;
			brokers = new ArrayList<String>();
			this.server = new ServerSocket(5000);
			System.out.println("--->Broker Maestro inició...");
			
			while(true) {
				System.out.println("Waiting for message from broker...");
				this.socket = this.server.accept();
				System.out.println("Broker accepted...");
				new ConnectionBrokerInit(this.socket, this);	
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addBroker(String broker) {
		this.brokers.add(broker);
		this.brokerCount++;
	}
	
	public int getBrokerCount() {
		return this.brokerCount;
	}
	
	public ArrayList<String> getBrokers() {
		return this.brokers;
	}

	public static void main(String[] args) {
		new BrokerInit();
	}

}
