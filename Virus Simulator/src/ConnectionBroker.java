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
	
}
