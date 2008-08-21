package dacpclient;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import playlist.Track;

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
	
	public void play() {
		send(DACPClientBroadcaster.play());
	}
	public void pause() {
		send(DACPClientBroadcaster.pause());
	}
    
    public void skip(){
    	send(DACPClientBroadcaster.skip());
    }
    
    public String getXML(String key){
        return null;
    }
	
	public void setVolume(double newVolume) {
		send(DACPClientBroadcaster.changeVolume(newVolume));
	}
	
	public int getVolume(){
		//TODO
		return 0;
	}
	
	public List<Track> getTracks(){
		//TODO
		return null;
	}
	
	public void setTracks(List<Track> l){
		//TODO
	}
	
	private void send(String command) {
		p.println("GET /ctrl-int/1/" + command + " HTTP/1.1\r\n");
	}

	public static void main(String[] args) throws UnknownHostException, IOException{
		new DACPClient("climie", 51234);
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}

}
