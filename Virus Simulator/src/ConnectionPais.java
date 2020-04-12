import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ConnectionPais {
	
	private Socket socket;
	private Broker broker;
	private DataInputStream input;
	private DataOutputStream output;
	private Scanner scn;
	
	public ConnectionPais(Socket pSocket, Broker pBroker) {
		try {
			this.socket = pSocket;
			this.broker = pBroker;
			this.scn = new Scanner(System.in);
			this.input = new DataInputStream(this.socket.getInputStream());
			this.output = new DataOutputStream(this.socket.getOutputStream());
			
			Thread sendMessage = new Thread(new Runnable() {
				public void run() {
					while(true) {
						try {
							System.out.println("Message to send...");
							String msg = scn.nextLine();
							output.writeUTF(msg);
						} catch (IOException e) {
							System.out.println("Error sendMessage - IOException");
						}
					}
				}
			});
			
			Thread readMessage = new Thread(new Runnable() {
				public void run() {
					while(true) {
						try {
							String msg = input.readUTF();
							System.out.println("Message received from another broker: " + msg);
						} catch (IOException e) {
							System.out.println("Error readMessage - IOException");
						}
					}
				}
			});
			sendMessage.start();
			readMessage.start();
			
		} catch (IOException e) {
			System.out.println("Error constructorConnectionPairs - IOException");
		}
	}

}
