import java.io.Serializable;


public class Mensaje implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int codigoBrokerPais;
	private int tipo;
	private Object contenidoMensaje;
	
	public Mensaje(int codigoBrokerPais, int tipo, Object contenidoMensaje) {
		super();
		this.codigoBrokerPais = codigoBrokerPais;
		this.tipo = tipo;
		this.contenidoMensaje = contenidoMensaje;
	}

	public Mensaje() {
		super();
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public Object getContenidoMensaje() {
		return contenidoMensaje;
	}

	public void setContenidoMensaje(Object contenidoMensaje) {
		this.contenidoMensaje = contenidoMensaje;
	}

	public int getcodigoBrokerPais() {
		return codigoBrokerPais;
	}

	public void setcodigoBrokerPais(int codigoBrokerPais) {
		this.codigoBrokerPais = codigoBrokerPais;
	}

	@Override
	public String toString() {
		return "Mensaje{" + "codigoBrokerPais=" + codigoBrokerPais + ", tipo=" + tipo + ", contenidoMensaje=" + contenidoMensaje + '}';
	}

}