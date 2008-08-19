package dacp;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import dacp.ServerParser;

public class DACPServer {

	private final int PORT;

	private final ServerSocket SERVER_SOCK;

	public DACPServer(int port) throws IOException {
		this.PORT = port;
		SERVER_SOCK = new ServerSocket(PORT);
		System.out.println("Server starting.\n--------\n");
		listen();
	}

	private void listen() throws IOException {
		while (true) {
			System.out.println("Waiting for connection.");
			new Thread(new ServerThread(SERVER_SOCK.accept())).start();
			System.out.println("Accepting connections.");
		}
	}

//	public static void main(String[] args) throws IOException {
//
//		DACPServer s;
//
//		s = new DACPServer(51234);
//		s.listen();
//	}

	private class ServerThread implements Runnable {

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
								ServerParser.parse(parseText);
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

		private ServerThread(Socket s) {
			this.SOCK = s;
		}

	}
}
