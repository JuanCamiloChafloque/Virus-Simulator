import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionBroker extends Thread{
	
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private Broker broker;
	
	public ConnectionBroker(Socket pSocket, Broker pBroker) {
		
		this.socket = pSocket;
		this.broker = pBroker;
		try {
			this.input = new ObjectInputStream(this.socket.getInputStream());
			this.output = new ObjectOutputStream(this.socket.getOutputStream());
			this.start();
		} catch (IOException e) {
			System.out.println("Error constructorConnectionBroker - IOException: " + e.getMessage());
		}
	}
	
	public void run() {
		try {
			Message message = (Message) this.input.readObject();
			switch(message.getType()) {	
				//Agregar a otros brokers conectados
				case 1:
					System.out.println("Adding brokers...");
					this.broker.getBrokers().clear();
					@SuppressWarnings("unchecked") ArrayList<InformationBroker> brok = (ArrayList<InformationBroker>) message.getContent();
					for(InformationBroker actual: brok) {
						if(actual.getPort() != this.broker.getPort()) {
							this.broker.addBroker(actual);
						}
					}
					for(InformationBroker actual: this.broker.getBrokers()) {
						System.out.println("Tengo al broker con id " + actual.getIdBroker() + " y puerto " + actual.getPort());
					}
					break;
				
				//Agregar paises
				case 2:
					System.out.println("Adding paises...");
					@SuppressWarnings("unchecked") ArrayList<Pais> p = (ArrayList<Pais>) message.getContent();
					for(Pais actual: p) {
						if(!this.broker.getPaises().contains(actual)) {
							this.broker.addPais(actual);
						}
					}
					for(Pais actual: this.broker.getPaises()) {
						System.out.println("Tengo al pais con nombre: " + actual.getNombre());
					}
					break;
			}
		} catch (IOException e) {
			System.out.println("Error runConnectionBroker - IOException: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println("Error runConnectionBroker - ClassNotFoundException: " + e.getMessage());
		}
	}
}
