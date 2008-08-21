package dacpwriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import daccpserver.command.Pause;
import daccpserver.command.Play;
import daccpserver.command.RequestPlaylist;
import daccpserver.command.SetVolume;
import daccpserver.command.Skip;

public class DACPWriter {

	private final String HOST;
	private final int PORT;
	private final PrintStream p;
	private Socket sock;

	public DACPWriter(String host, int port) throws UnknownHostException, IOException {
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
		sendCommand(new Play().toCommandString());
	}
	public void pause() {
		sendCommand(new Pause().toCommandString());
	}
    
    public void skip(){
    	sendCommand(new Skip().toCommandString());
    }
    
    public String getXML(String key){
        return null;
    }
	
	public void setVolume(double newVolume) {
		sendCommand(new SetVolume(newVolume).toCommandString());
	}
	
	public void requestPlaylist(){
		sendCommand(new RequestPlaylist().toCommandString());
	}
	
	public void sendCommand(String command) {
		p.println("GET /ctrl-int/" + command + " HTTP/1.1\r\n");
	}
	
	public void sendData(DACPEntry data){
		p.println(data.write());
	}

	public static void main(String[] args) throws UnknownHostException, IOException{
		new DACPWriter("climie", 51234);
	}

}
