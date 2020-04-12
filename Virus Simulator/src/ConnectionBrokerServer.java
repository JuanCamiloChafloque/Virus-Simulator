import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectionBrokerServer implements Runnable{
	
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	private Broker broker;
	
	public ConnectionBrokerServer(Socket pSocket, Broker pBroker) {
		
		this.socket = pSocket;
		this.broker = pBroker;
		try {
			this.input = new DataInputStream(this.socket.getInputStream());
			this.output = new DataOutputStream(this.socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Error constructorConnectionBroker - IOException: " + e.getMessage());
		}
	}
	
	public void run() {
		try {
			String msgReceived = this.input.readUTF();
			System.out.println("Message from broker client: " + msgReceived);
			this.output.writeUTF("Broker Master has received your message...");
			this.output.close();
			this.input.close();
		} catch (IOException e) {
			System.out.println("Error runConnectionBrokerInit - IOException: " + e.getMessage());
		}
	}
}
