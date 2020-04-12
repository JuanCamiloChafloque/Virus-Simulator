import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectionInit implements Runnable{
	
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	private BrokerInit brokerMaster;
	
	public ConnectionInit(Socket pSocket, BrokerInit pBroker) {
		
		this.socket = pSocket;
		this.brokerMaster = pBroker;
		try {
			this.input = new DataInputStream(this.socket.getInputStream());
			this.output = new DataOutputStream(this.socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Error constructorConnectionBrokerInit - IOException: " + e.getMessage());
		}
	}
	
	public void run() {
		try {
			String msgReceived = this.input.readUTF();
			System.out.println("Message from broker: " + msgReceived);
			this.output.writeUTF("Broker Master has added you to the list...");
			while(true) {
				if(BrokerInit.MIN == this.brokerMaster.getBrokerCount()) {
					System.out.println("Minimum Brokers achieved. Master Broker sending IPs...");
					for(ConnectionInit envio: this.brokerMaster.getBrokers()) {
						for(ConnectionInit mensaje: this.brokerMaster.getBrokers()) {
							if(envio != mensaje) {
								System.out.println("Sending broker " + envio.socket.getInetAddress().getHostAddress() + " the broker with IP: " + mensaje.socket.getInetAddress().getHostAddress() + "...");
								envio.output.writeUTF(mensaje.socket.getInetAddress().getHostAddress());
							}
						}
						envio.output.writeUTF("Master Broker finished...");
					}
					break;
				}
			}
		} catch (IOException e) {
			System.out.println("Error runConnectionBrokerInit - IOException: " + e.getMessage());
		}
	}
}
