import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectionBrokerInit extends Thread{
	
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	private BrokerInit brokerMaster;
	
	public ConnectionBrokerInit(Socket pSocket, BrokerInit pBroker) {
		
		this.socket = pSocket;
		this.brokerMaster = pBroker;
		try {
			this.input = new DataInputStream(this.socket.getInputStream());
			this.output = new DataOutputStream(this.socket.getOutputStream());
			this.start();
		} catch (IOException e) {
			System.out.println("Error - IOException: " + e.getMessage());
		}
	}
	
	public void run() {
		
		try {
			String msgReceived = this.input.readUTF();
			System.out.println("Message from broker: " + msgReceived);
			this.output.writeUTF("Send IP Adress...");
			String msgIP = this.input.readUTF();
			System.out.println("IP from Broker: " + msgIP);
			this.brokerMaster.addBroker(msgIP);
			while(true) {
				if(this.brokerMaster.MIN == this.brokerMaster.getBrokerCount()) {
					System.out.println("Minimum Brokers achieved. Master Broker sending IPs...");
					for(String actual: this.brokerMaster.getBrokers()) {
						System.out.println("Sending to broker IP: " + actual + "...");
						this.output.writeUTF(actual);
					}
					break;
				}
			}

		} catch (IOException e) {
			System.out.println("Error - IOException: " + e.getMessage());
		}
		
	}

}
