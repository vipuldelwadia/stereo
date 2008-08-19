package daap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import music.Lackey;

public class Handshake implements Runnable{

	private ServerSocket connection;
	private final int DAAPPORT = 3689;
	private final int PORT = 8080;
	private BufferedReader netInput = null;
	private Lackey lackey;
	
	public Handshake(Lackey l) throws IOException{
		connection = new ServerSocket(PORT);
		this.lackey = l;
	}

	private DaapClient createConnection() {
		try {
			Socket client = connection.accept();
			netInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
				if(netInput.readLine().equals("GET /")){
					//getInetAddress appends "/" to the start of a string
					String DAAPServer = client.getInetAddress().toString().substring(1);
					client.close();
					return new DaapClient(DAAPServer, DAAPPORT);
				}
				
			client.close();
			// Fails if wrong message from client
			return null;

		} catch (IOException e) {
			// Fails if the socket is already busy, or if the DaapClient constructor fails
			return null;
		} catch (NullPointerException e) {
			// Fails if the socket closes before input
			return null;
		}
	}

	public void run() {
		while(true){
			DaapClient client = createConnection();
			lackey.newConnection(client);
		}
	}
}
