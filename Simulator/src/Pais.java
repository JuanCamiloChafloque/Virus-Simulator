import java.io.Serializable;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Pais extends Thread implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String nombre;
	private double pobl;
	private double porInfected;
	private double porVulnerable;
	private double porRecuperados;
	private ArrayList<Pais> neigh;
	private Broker broker;
	private double peso;
	private int port;
	private int idBroker;
	
	public Pais(int id, String nombre, double pobl, double porInfected, double porVulnerable, double porRecuperados, int port ) throws UnknownHostException {
		this.idBroker = id;
		this.nombre = nombre;
		this.pobl = pobl;
		this.porInfected = porInfected;
		this.porVulnerable = porVulnerable;
		this.porRecuperados = porRecuperados;
		this.peso = 0;
		this.port = port;
	}
	
	@Override
	public void run() {
		System.out.println(this.nombre + " init server...");
		while(true) {
//			for(Pais paisActual: this.neigh) {
//				
//			}
		}
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

	public double getPorInfected() {
		return porInfected;
	}

	public void setPorInfected(double porInfected) {
		this.porInfected = porInfected;
	}

	public double getPorVulnerable() {
		return porVulnerable;
	}

	public void setPorVulnerable(double porVulnerable) {
		this.porVulnerable = porVulnerable;
	}

	public double getPorRecuperados() {
		return porRecuperados;
	}

	public void setNumRecuperados(double porRecuperados) {
		this.porRecuperados = porRecuperados;
	}

	public Broker getBroker() {
		return broker;
	}
	
	public void setBroker(Broker pBroker) {
		this.broker = pBroker;
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
	
	public int getIdBroker() {
		return idBroker;
	}

	public void setIdBroker(int idBroker) {
		this.idBroker = idBroker;
	}

	public void actualizarPeso() {
		
	}
	
	public double getPeso() {
		return peso;
	}
	
	
	public void initServer() {
	}

}
