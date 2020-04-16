import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Broker extends Thread {

	private ArrayList<InfoBroker> brokers;
	private ArrayList<Pais> paises;
	private String ip;
	private Socket clientInitSocket;
	private ObjectOutputStream outputClient;
	private ObjectInputStream inputClient;
	private final static int SERVER_PORT = 5000;
	private final static int PUERTO_SERVICIO = 7000;
	private ServerSocket listenSocket;
	private Broker broker;
	private InfoBroker myInfo;
	private InfoBroker auxChange;
	private Boolean estadoBalanceo;


	public Broker() {
		try {
			this.broker = this;
			this.brokers = new ArrayList<InfoBroker>();
			this.paises = new ArrayList<Pais>();
			this.clientInitSocket = new Socket("192.168.0.19", Broker.SERVER_PORT);
			this.outputClient = new ObjectOutputStream(this.clientInitSocket.getOutputStream());
			this.inputClient = new ObjectInputStream(this.clientInitSocket.getInputStream());
			this.ip = "192.168.0.19";
			this.estadoBalanceo = false;
			System.out.println("Mensaje inicial recibido del Broker Maestro: " + inputClient.readObject());
			this.outputClient.writeObject(Broker.PUERTO_SERVICIO);
			this.outputClient.writeObject(ip);
			System.out.println("Broker inició...");
			waitBrokers();
		} catch (IOException e) {
			System.out.println("Error IOException in Broker()" + e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println("Error ClassNotFoundException in Broker()" + e.getMessage());
		}
	}

	public String getIP() {
		return this.ip;
	}

	public ArrayList<Pais> getPaises() {
		return this.paises;
	}

	public ArrayList<InfoBroker> getBrokers() {
		return this.brokers;
	}

	public void addPais(Pais pais) {
		this.paises.add(pais);
	}

	public void waitBrokers() {

		Thread esperar = new Thread(new Runnable() {
			@Override
			public void run() {
				Socket cliente = null;
				try {
					listenSocket = new ServerSocket(Broker.PUERTO_SERVICIO);
				} catch (IOException e) {
					System.out.println("Error IOException in waitBrokers() from Broker " + e.getMessage());
				}
				while (true) {
					try {
						System.out.println("Esperando mensaje de Broker/País.....");
						cliente = listenSocket.accept();
						ObjectInputStream dIn = new ObjectInputStream(cliente.getInputStream());
						Mensaje mensaje = (Mensaje) dIn.readObject();
						System.out.println("Se recibió un mensaje de tipo: " + mensaje.getTipo() + " del broker con id: " + mensaje.getcodigoBrokerPais());
						switch (mensaje.getTipo()) {

						case 1:
							brokers.clear();
							@SuppressWarnings("unchecked") ArrayList<InfoBroker> brokersMeet = (ArrayList<InfoBroker>) mensaje.getContenidoMensaje();
							for (InfoBroker ib : brokersMeet) {
								if (!ib.getIpBroker().getHostAddress().equals(ip)) {
									brokers.add(ib);
								} else {
									if (ib.getPortServer() != Broker.PUERTO_SERVICIO) {
										brokers.add(ib);
									}
									else {
										myInfo = new InfoBroker(ib.getIpBroker(), ib.getPortServer(), ib.getCodigoBroker());
									}
								}
							}
							break;
							
						case 2:

							@SuppressWarnings("unchecked") ArrayList<Pais> paisesIniciales = (ArrayList<Pais>) mensaje.getContenidoMensaje();
							for (Pais actual : paisesIniciales) {
								if (!paises.contains(actual)) {
									paises.add(actual);
								}
							}
							System.out.println("Cantidad de paises del broker: " + myInfo.getCodigoBroker() + " es de: " + paises.size() + "\n");
							break;

						case 3:
							
							broker.inits();
							break;
							
						case 4:
							System.out.println("Me pidieron mi cantidad de paises");
							InfoBroker ib= (InfoBroker) mensaje.getContenidoMensaje();
							MensajeBalanceo mb = new MensajeBalanceo(myInfo, paises.size());
							enviarMensaje(ib, 5, mb) ;
							break;
							
						case 5:
							
							MensajeBalanceo mbs = (MensajeBalanceo) mensaje.getContenidoMensaje();
							if(paises.size() - mbs.getCantPais() >= 2 && estadoBalanceo==false) {
								auxChange = mbs.getInfoBroker();
								estadoBalanceo = true;
								procesoBalanceo();
							}
							break;
							
						case 6:
							
							Pais p = paises.get(0);
							System.out.println("Voy a enviar a " + p.getNombre() + " al Broker : " + auxChange.getCodigoBroker());
							enviarMensaje(auxChange, 7, p);
							System.out.println("Broker " + myInfo.getCodigoBroker() + " eliminando al país " + p.getNombre() + " de su lista...");
							paises.remove(0);
							estadoBalanceo = false;
							break;
							
						case 7:
							
							Pais newP = (Pais) mensaje.getContenidoMensaje();
							System.out.println("Voy a instanciar al país: " + newP.getNombre());
							newP.setMyBroker(myInfo);
							newP.setIpPais(ip);
							paises.add(newP);
							newP.restart();
							break;
						}
						
					} catch (IOException e) {
						System.out.println("Error IOException in waitBrokers() from Broker " + e.getMessage());
					} catch (ClassNotFoundException e) {
						System.out.println("Error ClassNotFoundException in waitBrokers() from Broker " + e.getMessage());
					}
				}
			}
		});
		esperar.start();
	}
	
	private void procesoBalanceo() {
		Pais p = this.paises.get(0);
		p.freeze();
	}
	
	public void inits() {
		this.start();
	}

	public void run() {
		
		for (Pais p : this.paises) {
			p.setMyBroker(this.myInfo);
			p.init();
			System.out.println("El broker " + this.myInfo.getCodigoBroker() + " a iniciado a: " + p.getNombre() + " con la ip: "+ p.getIpPais() + " y el puerto: " + p.getPort() );
		}

		while (true) {
			try {
				sleep(20000);
				for(InfoBroker ib:this.getBrokers()) {
					enviarMensaje(ib, 4, this.myInfo);
				}
			} catch (InterruptedException e) {
				System.out.println("Error InterruptedException in run() from Broker " + e.getMessage());
			}
		}
	}

	public void enviarMensaje(InfoBroker destinario, int tipo, Object contenidoMensaje) {
		Mensaje mensaje = null;
		ObjectOutputStream output = null;
		System.out.println("Enviando mensaje a broker con: " + destinario.getCodigoBroker() + ". El mensaje es de tipo: " + tipo);
		System.out.println("Información del broker receptor. IP: " + destinario.getIpBroker() + ". Puerto: " + destinario.getPortServer());
		try {
			Socket sc = new Socket(destinario.getIpBroker(), destinario.getPortServer());
			mensaje = new Mensaje(destinario.getCodigoBroker(), tipo, contenidoMensaje);
			output = new ObjectOutputStream(sc.getOutputStream());
			output.writeObject(mensaje);
			sc.close();
		} catch (IOException e) {
			System.out.println("Error IOException in enviarMensaje() from Broker " + e.getMessage());
		}
	}

	/**
	 * Main Broker
	 * @param args
	 */
	public static void main(String[] args) {
		new Broker();
	}
}