import java.io.Serializable;

public class MensajeBalanceo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	InfoBroker infoBroker;
	int cantPais;

	public MensajeBalanceo(InfoBroker infoBroker, int cantPais)  {
		super();
		this.infoBroker = infoBroker;
		this.cantPais = cantPais;
	}
	
	public InfoBroker getInfoBroker() {
		return infoBroker;
	}
	
	public void setInfoBroker(InfoBroker infoBroker) {
		this.infoBroker = infoBroker;
	}
	
	public int getCantPais() {
		return cantPais;
	}
	
	public void setCantPais(int cantPais) {
		this.cantPais = cantPais;
	}

}