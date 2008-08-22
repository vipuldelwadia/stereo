package music;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import reader.DACPRequestParser;
import util.command.Command;
import util.command.DACPCommand;
import util.command.DACPPause;
import util.command.DACPPlay;
import util.command.DACPSetVolume;
import util.command.Pause;
import util.command.Play;
import util.command.SetVolume;
import util.command.Skip;



public class DACPDJ {

	private final int PORT;

	private final ServerSocket SERVER_SOCK;
	
	private final DJ dj;
	
	public DACPDJ(int port, DJ dj) throws IOException {
		this.PORT = port;
		this.dj = dj;
		SERVER_SOCK = new ServerSocket(PORT);
		System.out.println("Server starting.\n--------\n");
		listen();
	}

	private void listen() throws IOException {
		new Thread(){
			public void run(){
				while (true) {
					System.out.println("Waiting for connection.");
					try {
						new Thread(new ServerRunnable(SERVER_SOCK.accept())).start();
					} catch (IOException e) {}
					System.out.println("Accepting connections.");
				}
			}
		}.start();
	}
	
	private Command convertCommand(DACPCommand s) {
		if(s instanceof DACPPause) return new Pause();
		if(s instanceof DACPPlay) return new Play();
		if(s instanceof DACPPlay) return new SetVolume(((DACPSetVolume)s).getVolume());
		if(s instanceof DACPPlay) return new Skip();
		return null;
	}

//	public static void main(String[] args) throws IOException {
//
//		DACPDJ s;
//
//		s = new DACPDJ(51234);
//		//s.listen();
//	}

	private class ServerRunnable implements Runnable {

		private final Socket SOCK;
		private final String responseOK = "HTTP/1.1 204 OK\r\n\r\n";

		public void run() {
			try {
				final PrintStream p = new PrintStream(SOCK.getOutputStream());
				
				new Thread() {
					public void run() {

						try {
							Scanner scan = new Scanner(SOCK.getInputStream());
							while (scan.hasNextLine()){
								String parseText="";
								while(true){
									String current = scan.nextLine();
									if(current.equals("")) break;
									parseText+=current;
								}
								// TODO deal with malformed requests
								DACPCommand s = DACPRequestParser.parse(parseText);
								
								Command c = convertCommand(s);
								
								if(c != null) c.doAction(dj);
								
								//send response
								p.print(responseOK);
							}
						} catch (IOException e) {
							e.printStackTrace();
						} catch (NoSuchElementException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}.start(); //Override the run method.

				
			}catch (IOException e){
				e.printStackTrace();
			}
		}

		private ServerRunnable(Socket s) {
			this.SOCK = s;
		}

	}
}
