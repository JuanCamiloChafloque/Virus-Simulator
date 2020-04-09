import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectionBroker extends Thread {
	
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	private Broker broker;
	
	public ConnectionBroker (Socket pSocket, Broker pBroker) {
		this.socket = pSocket;
		this.broker = pBroker;
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
			this.output.writeUTF("Broker asking for confirmation...");
			String msgReceived = this.input.readUTF();
			System.out.println("Message received from Master Broker: " + msgReceived);
			this.output.writeUTF(this.broker.getIP().getHostAddress());
			System.out.println("Sending IP Address...");
			while(true) {
				String msgRequest = this.input.readUTF();
				System.out.println("Message received from master broker: " + msgRequest);
				if(!this.broker.getIP().getHostAddress().equals(msgRequest)) {
					this.broker.addBroker(msgRequest);
					System.out.println("Broker added another broker with IP: " + msgRequest);
				}
			}

			
		} catch (IOException e) {
			System.out.println("Error - IOException: " + e.getMessage());
		}
			
	}
	
}
