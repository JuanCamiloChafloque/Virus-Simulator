import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ConnectionBrokerClient implements Runnable{
	
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	private Scanner scn;
	
	public ConnectionBrokerClient(Socket pSocket) {
		
		this.socket = pSocket;
		this.scn = new Scanner(System.in);
		try {
			this.input = new DataInputStream(this.socket.getInputStream());
			this.output = new DataOutputStream(this.socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Error constructorConnectionBroker - IOException: " + e.getMessage());
		}
	}
	
	public void run() {
		try {
			System.out.println("Message to send...");
			String msg = this.scn.nextLine();
			this.output.writeUTF(msg);
			String msgReceived = this.input.readUTF();
			System.out.println("Message received from broker server: " + msgReceived);
			this.output.close();
			this.input.close();
			this.scn.close();
		} catch (IOException e) {
			System.out.println("Error runConnectionBrokerInit - IOException: " + e.getMessage());
		}
	}
}
