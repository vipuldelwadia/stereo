package music;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import reader.DACPRequestParser;
import util.command.DACPCommand;



public class DACPDJ {

	private final int PORT;

	private final ServerSocket SERVER_SOCK;
	
	public DACPDJ(int port) throws IOException {
		this.PORT = port;
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

	public static void main(String[] args) throws IOException {

		DACPDJ s;

		s = new DACPDJ(51234);
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
								DACPCommand s = DACPRequestParser.parse(parseText);
								// TODO do something with this
								
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
