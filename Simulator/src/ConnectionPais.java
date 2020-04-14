import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionPais {
	
	private Socket socket;
	private Pais pais;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	public ConnectionPais(Socket pSocket, Pais pPais) {
		
		try {
			this.socket = pSocket;
			this.pais = pPais;
			this.output = new ObjectOutputStream(this.socket.getOutputStream());
			this.input = new ObjectInputStream(this.socket.getInputStream());
		} catch (IOException e) {
			System.out.println("Error ConnectionPais - IOException " + e.getMessage());
		}		
		
	}

}
