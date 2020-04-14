import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.Socket;

public class ConnectionInit extends Thread{
	
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private BrokerInit brokerMaster;
	
	public ConnectionInit(Socket pSocket, BrokerInit pBroker) {
		
		this.socket = pSocket;
		this.brokerMaster = pBroker;
		try {
			this.input = new ObjectInputStream(this.socket.getInputStream());
			this.output = new ObjectOutputStream(this.socket.getOutputStream());
			this.start();
		} catch (IOException e) {
			System.out.println("Error constructorConnectionBrokerInit - IOException: " + e.getMessage());
		}
	}
	
	public void run() {
		try {
			Inet4Address msgIp = (Inet4Address) this.input.readObject();
			int msgPort = (Integer) this.input.readObject();
			System.out.println("Received information from broker. Creating InfoBroker...");
			InformationBroker info = new InformationBroker(msgIp, msgPort, (this.brokerMaster.getBrokerCount() + 1));
			System.out.println("Assigning id to broker...");
			this.output.writeObject(("" + (this.brokerMaster.getBrokerCount() + 1)));
			System.out.println("Adding broker with id " + info.getIdBroker() + " and port " + info.getPort() + " to list...");
			this.brokerMaster.addBroker(info);
			System.out.println("Sending paises to broker with id " + info.getIdBroker());
			this.brokerMaster.sendPaises(info);
			while(true) {
				if(BrokerInit.MIN == this.brokerMaster.getBrokerCount()) {
					System.out.println("Minimum brokers achieved. Broker master sending ips...");
					this.brokerMaster.sendIps(info);
					break;
				}
			}
		} catch (IOException e) {
			System.out.println("Error runConnectionBrokerInit - IOException: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println("Error runConnectionBrokerInit - ClassNotFoundException: " + e.getMessage());
		}
	}
}
