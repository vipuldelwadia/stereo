package player;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class DACPClient {

	private final String HOST;
	private final int PORT;
	private Socket sock;

	public DACPClient(String host, int port) throws UnknownHostException, IOException {
		this.HOST = host;
		this.PORT = port;
		connect();
	}

	private void connect() throws UnknownHostException, IOException {
		sock = new Socket(HOST, PORT);
//		new Thread() {
//			public void run() {
//				try {
//					Scanner scan = new Scanner(sock.getInputStream());
//					while(scan.hasNext()) System.out.println(scan.next());
//				} catch (IOException e) {
//					e.printStackTrace();
//				}	
//			}
//		}.start();
	}
	
	public void play() throws IOException {		
		PrintStream p = new PrintStream(sock.getOutputStream());
		p.println("GET /ctrl-int/1/playpause HTTP/1.1\r\n");
	}
	
	public void pause() throws IOException {
		PrintStream p = new PrintStream(sock.getOutputStream());
		p.println("GET /ctrl-int/1/pause HTTP/1.1\r\n");
	}

	public static void main(String[] args) throws UnknownHostException, IOException{
		new DACPClient("cafe-baba", 51234);
	}
}
