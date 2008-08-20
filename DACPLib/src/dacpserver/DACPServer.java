package dacpserver;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

import daccpserver.command.DACPServerCommandInterface;


public class DACPServer {

	private final int PORT;

	private final ServerSocket SERVER_SOCK;

	private final Set<DACPServerListener> listeners;
	
	public DACPServer(int port) throws IOException {
		this.PORT = port;
		SERVER_SOCK = new ServerSocket(PORT);
		System.out.println("Server starting.\n--------\n");
		listeners = new HashSet<DACPServerListener>();
		listen();
	}
	
	public void addServerListener(DACPServerListener s) {
		listeners.add(s);
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

	public static void main(String[] args) throws IOException {

		DACPServer s;

		s = new DACPServer(51234);
		//s.listen();
	}

	private class ServerRunnable implements Runnable {

		private final Socket SOCK;

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
								DACPServerCommandInterface s = DACPServerParser.parse(parseText);
								for (DACPServerListener l : listeners) {
									s.doAction(l);
								}
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
