package dacp.client;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class DACPClient {

	private final String HOST;

	private final int PORT;

	public DACPClient(String host, int port) {
		this.HOST = host;
		this.PORT = port;
	}

	private void connect() throws UnknownHostException, IOException {
		final Socket sock = new Socket(HOST, PORT);
		new PrintStream(sock.getOutputStream()).println(" Hello.");
		new Thread() {
			public void run() {
				try {
					Scanner scan = new Scanner(sock.getInputStream());
					while(scan.hasNext()) System.out.println(scan.next());
				} catch (IOException e) {
					System.err.println("Probably lost connection with DACP server");
					//e.printStackTrace();
				}	
			}
		}.start();
	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		DACPClient c = new DACPClient("localhost", 51234);
		c.connect();
	}

}
