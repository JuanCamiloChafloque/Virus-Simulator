import java.io.Serializable;

public class Message implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int idBroker;
	private int type;
	private Object content;
	
	public Message(int idBroker, int type, Object content) {
		this.idBroker = idBroker;
		this.type = type;
		this.content = content;
	}
	
	public int getIdBroker() {
		return idBroker;
	}
	public void setIdBroker(int idBroker) {
		this.idBroker = idBroker;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Object getContent() {
		return content;
	}
	public void setContent(Object content) {
		this.content = content;
	}
}
