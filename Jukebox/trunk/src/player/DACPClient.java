package src.player;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class DACPClient {

	private final String HOST;
	private final int PORT;
	private final PrintStream p;
	private Socket sock;

	public DACPClient(String host, int port) throws UnknownHostException, IOException {
		this.HOST = host;
		this.PORT = port;
		connect();
		this.p = new PrintStream(sock.getOutputStream());
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
		p.println("GET /ctrl-int/1/playpause HTTP/1.1\r\n");
	}
	
	public void pause() throws IOException {
		p.println("GET /ctrl-int/1/pause HTTP/1.1\r\n");
	}

	public void changeVolume(int newVolume) {
		p.println("GET /ctrl-int/1/setproperty?dmcp.volume=" + newVolume*10 + ".00000 HTTP/1.1\r\n");
	}

	public static void main(String[] args) throws UnknownHostException, IOException{
		new DACPClient("cafe-baba", 51234);
	}

}
