import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Pais extends Thread implements Serializable {

	private static final long serialVersionUID = 1L;
	private int cantDel = 0;
	private String nombre;
	private double pobl;
	private double numInfected;
	private double numVulnerable;
	private double numRecuperados;
	private ArrayList<Pais> neigh;
	private double peso;
	private int port;
	private String ipPais;
	private int codigoPais;
	private int numBroker;
	private double beta;
	private double gama;
	private double media;
	private double numInfectedAntes;
	private InfoBroker myBroker;
	private boolean exit = true;


	public Pais(int numBroker, String name, double pobl, double numInfected, double numVulnerable, double numRecuperados, 
			int port,double pBeta, double pGama, double pMedia,String pIp) throws UnknownHostException {

		this.numInfectedAntes = 1;
		this.numVulnerable = 10;
		this.numRecuperados = 0;
		this.numInfected = 0;
		this.setNumBroker(numBroker);
		this.nombre = name;
		this.pobl = pobl;
		this.numInfected = numInfected;
		this.numVulnerable = numVulnerable;
		this.numRecuperados = numRecuperados;
		this.peso = 0;
		this.port = port;
		this.neigh = new ArrayList<Pais>();
		this.beta = pBeta;
		this.gama = pGama;
		this.media = pMedia;
		this.ipPais = pIp;
	}

	public InfoBroker getMyBroker() {
		return myBroker;
	}

	public void setMyBroker(InfoBroker myBroker) {
		this.myBroker = myBroker;
	}

	public String getIpPais() {
		return ipPais;
	}

	public void setIpPais(String ipPais) {
		this.ipPais = ipPais;
	}

	public void init() {
		this.start();
	}

	public void waitNeigh() {

		Thread esperar = new Thread(new Runnable() {
			ServerSocket listenSocket;
			@Override
			public void run() {
				listenSocket = null;
				Socket cliente = null;
				try {
					listenSocket = new ServerSocket(port);
				} catch (IOException e) {
					System.out.println("Error IOException in waitNeigh() from Pais " + e.getMessage());
				}
				while (exit) {
					try {

						System.out.println("Esperando mensaje de Broker/País vecino");
						cliente = listenSocket.accept();
						ObjectInputStream dIn = new ObjectInputStream(cliente.getInputStream());
						Mensaje mensaje = (Mensaje) dIn.readObject();
						System.out.println(nombre + " recibió un mensaje de tipo: " + mensaje.getTipo());
						switch (mensaje.getTipo()) {

						case 1:

							String s=(String) mensaje.getContenidoMensaje();
							System.out.println(nombre + " tiene un mensaje de un vecino que dice: " + s);
							break;

						case 2:

							Double d = (Double) mensaje.getContenidoMensaje();
							System.out.println("Mi pais vecino tiene " + d + " infectados");
							numInfected = Math.floor(numInfected + d * 0.2);
							System.out.println(nombre + " actualizó su información: Ahora tiene " + numInfected + " infectados");
							break;

						case 3:

							String delPais = (String) mensaje.getContenidoMensaje();
							Pais aux=null;
							for(Pais p:neigh) {
								if(p.getNombre().equals(delPais)) {
									aux = p;
								}
							}
							if(aux != null) {
								neigh.remove(aux);
								enviarMensajePais(aux, 4, nombre + " ha eliminado a " + aux.getNombre() + " satisfactoriamente..." );
							}
							break;

						case 4:

							cantDel++;
							if(cantDel == neigh.size()) {
								enviarMensajePaisBroker(myBroker, 6, nombre + " está listo para la transferencia de máquina...");
								step();
							}
							break;

						case 5:

							Pais actP = (Pais) mensaje.getContenidoMensaje();
							System.out.println("Se actualizo el pais con la ip: " + actP.getIpPais());
							neigh.add(actP);
							break;

						}

					} catch (IOException e) {
						System.out.println("Error IOException in waitNeigh() from Pais " + e.getMessage());
					} catch (ClassNotFoundException e) {
						System.out.println("Error ClassNotFound in waitNeigh() from Pais " + e.getMessage());
					} catch (Throwable e) {
						System.out.println("Error Throwable in waitNeigh() from Pais " + e.getMessage());
					}
				}
				try {
					listenSocket.close();
				} catch (IOException e) {
					System.out.println("Error IOException in waitNeigh() from Pais " + e.getMessage());
				}
			}
		});
		esperar.start();
	}

	@Override
	public void run() {

		waitNeigh();
		while(this.exit) {
			try {
				sleep((long) (7000));
				System.out.println(nombre + " mandará un mensaje a sus vecinos...");
				for(Pais p : this.neigh){
					enviarMensajePais(p, 1, "Mensaje de tu vecino " + this.nombre);
				}
			} catch (InterruptedException e) {
				System.out.println("Error InterruptedException in run() from Pais " + e.getMessage());
			}

			if(this.numInfected > 0) {

				this.plusInfected();
				System.out.println(this.nombre + " tiene " + this.numInfected + " infectados actualmente...");
				if((Math.abs(this.numInfected - this.numInfectedAntes) / this.numInfected) > 0.5) {
					System.out.println(this.nombre + " enviando mensaje de alerta a sus vecinos...");
					for(Pais neigh:this.getNeigh()) {
						enviarMensajePais(neigh, 2, this.numInfected);
					}
				}
				try {
					sleep((long) (this.pobl / 10000));
				} catch (InterruptedException e) {
					System.out.println("Error InterruptedException in run() from Pais " + e.getMessage());
				}
			}
		}
	}
	public void enviarMensajePais(Pais destinario, int tipo, Object contenidoMensaje) {
		Mensaje mensaje = null;
		ObjectOutputStream output = null;
		try {
			System.out.println("Enviando mensaje a: " + destinario.getNombre() + ". El mensaje es de tipo: " + tipo);
			Socket sc = new Socket(destinario.getIpPais(), destinario.getPort());
			mensaje = new Mensaje(this.getNumBroker(), tipo, contenidoMensaje);
			output = new ObjectOutputStream(sc.getOutputStream());
			output.writeObject(mensaje);
			sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public void enviarMensajePaisBroker(InfoBroker destinario, int tipo, Object contenidoMensaje) {
		Mensaje mensaje = null;
		ObjectOutputStream output = null;
		try {
			System.out.println("Enviando mensaje a: " + destinario.getCodigoBroker() + ". El mensaje es de tipo: " + tipo);
			Socket sc = new Socket(destinario.getIpBroker(), destinario.getPortServer());
			mensaje = new Mensaje(destinario.getCodigoBroker(), tipo, contenidoMensaje);
			output = new ObjectOutputStream(sc.getOutputStream());
			output.writeObject(mensaje);
			sc.close();
		} catch (IOException e) {
			System.out.println("Error IOException in enviarMensajePaisBroker() from Pais " + e.getMessage());
		}
	}

	public int getCodigoPais() {
		return codigoPais;
	}

	public void setCodigoPais(int codigoPais) {
		this.codigoPais = codigoPais;
	}

	public void plusInfected() {

		Double copia = null;
		copia = this.numInfected;
		this.numVulnerable = Math.floor(Math.random() * 6 + 1);
		this.numInfected = this.numInfected + Math.floor(this.beta * this.numVulnerable * this.numInfectedAntes);
		this.numRecuperados = Math.floor(this.gama * this.numInfectedAntes);
		this.numInfectedAntes = copia;

	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}

	public double getPobl() {
		return pobl;
	}

	public void setPobl(double pobl) {
		this.pobl = pobl;
	}

	public double getNumInfected() {
		return numInfected;
	}

	public void setNumInfected(double numInfected) {
		this.numInfected = numInfected;
	}

	public double getNumVulnerable() {
		return numVulnerable;
	}

	public void setNumVulnerable(double numVulnerable) {
		this.numVulnerable = numVulnerable;
	}

	public double getNumRecuperados() {
		return numRecuperados;
	}

	public void setNumRecuperados(double numRecuperados) {
		this.numRecuperados = numRecuperados;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public ArrayList<Pais> getNeigh() {
		return neigh;
	}

	public void addNeigh(Pais neigh) {
		this.neigh.add(neigh);
	}

	public double getPeso() {
		return peso;
	}

	public int getNumBroker() {
		return numBroker;
	}

	public void setNumBroker(int numBroker) {
		this.numBroker = numBroker;
	}

	public void freeze() {
		for(Pais p: this.neigh) {
			enviarMensajePais(p, 3, this.nombre);
		}
	}

	public void restart() {
		cantDel = 0;
		for(Pais p: this.neigh) {
			enviarMensajePais(p, 5, this);
		}
		this.exit = true;
		this.start();
	}

	public void step(){
		this.exit = false;
	}

}