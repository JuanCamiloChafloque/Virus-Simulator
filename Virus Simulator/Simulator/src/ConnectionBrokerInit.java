import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionBrokerInit extends Thread {

	private Socket clientSocket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private BrokerInit brokerMaestro;

	public ConnectionBrokerInit(BrokerInit broker) {
		this.brokerMaestro = broker;
	}

	public void reply(Socket clientSocket) {
		this.clientSocket = clientSocket;
		try {
			this.in = new ObjectInputStream(this.clientSocket.getInputStream());
			this.out = new ObjectOutputStream(this.clientSocket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Error IOException in reply() from ConnectionBrokerInit " + e.getMessage());
		}
		this.start();
	}

	public void run() {
		try {
			out.writeObject("Broker maestro me recibió...");
			Integer msgPuerto = null;
			String msgIp = null;
			try {
				msgPuerto = (Integer) this.in.readObject();
				msgIp = (String) this.in.readObject();
			} catch (ClassNotFoundException e) {
				System.out.println("Error ClassNotFoundException in run() from ConnectionBrokerInit " + e.getMessage());
			}
			InfoBroker nuevoBroker = new InfoBroker((Inet4Address) Inet4Address.getByName(msgIp), msgPuerto, (brokerMaestro.getBrokers().size() + 1));
			System.out.println("La información del nuevo broker: " + nuevoBroker.getCodigoBroker() + " es:" + nuevoBroker.getIpBroker().getHostAddress() + " Puerto: " + nuevoBroker.getPortServer());
			brokerMaestro.agregarBroker(nuevoBroker);
			System.out.println("Cantidad de brokers en el sistema: "+ brokerMaestro.getBrokers().size());
			System.out.println("Enviando paises a broker " + nuevoBroker.getCodigoBroker());
			this.enviarPaises(nuevoBroker);

			if(brokerMaestro.getBrokers().size() == BrokerInit.MIN) {
				System.out.println("Se llegó al número minimo de brokers. Enviando información de brokers...");
				this.sendIps(nuevoBroker);
				startBrokers();

			}
		} catch (IOException e) {
			System.out.println("Error IOException in run() from ConnectionBrokerInit " + e.getMessage());
		}
	}
	private void startBrokers() {
		for(InfoBroker ib: this.brokerMaestro.getBrokers()) {
			enviarMensaje(ib, 3, "");
		}
	}

	public void sendIps(InfoBroker destino) {
		for(InfoBroker ib: this.brokerMaestro.getBrokers()) {
			enviarMensaje(ib, 1, brokerMaestro.getBrokers());
		}
	}

	public void enviarPaises(InfoBroker destino) {
		ArrayList<Pais> aux = new ArrayList<Pais>();
		for (Pais pais: this.brokerMaestro.getPaises()) {
			if (pais.getNumBroker() == destino.getCodigoBroker()) {
				aux.add(pais);
			}
		}
		enviarMensaje(destino, 2, aux);
	}

	public void enviarMensaje(InfoBroker destinario, int tipo, Object contenidoMensaje) {
		Mensaje mensaje = null;
		ObjectOutputStream output = null;
		try {
			System.out.println("Enviando mensaje a broker con id: " + destinario.getCodigoBroker() + ". El mensaje es de tipo: " + tipo);
			Socket sc = new Socket(destinario.getIpBroker(), destinario.getPortServer());
			mensaje = new Mensaje(destinario.getCodigoBroker(), tipo, contenidoMensaje);
			output = new ObjectOutputStream(sc.getOutputStream());
			output.writeObject(mensaje);
			sc.close();
		} catch (IOException e) {
			System.out.println("Error IOException in enviarMensaje() from ConnectionBrokerInit " + e.getMessage());
		}

	}
}