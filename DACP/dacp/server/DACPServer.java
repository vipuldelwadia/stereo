package dacp.server;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import org.apache.commons.httpclient.HttpConnection;

public class DACPServer {

	private final int PORT;

	private final ServerSocket SERVER_SOCK;

	public DACPServer(int port) throws IOException {
		this.PORT = port;
		SERVER_SOCK = new ServerSocket(PORT);
		System.out.println("Server starting.\n--------\n");

	}

	private void listen() throws IOException {
		while (true) {
			System.out.println("Waiting for connection.");
			new Thread(new ServerThread(SERVER_SOCK.accept())).start();
			System.out.println("Accepting connections.");
		}
	}

	public static void main(String[] args) throws IOException {

		DACPServer s;

		s = new DACPServer(51234);
		s.listen();
	}

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
								String current=scan.nextLine();
								System.out.println(current);
								ServerParser.parse(current);
								//p.println("echo: "+ current);
							}
						} catch (IOException e) {
							//e.printStackTrace();
							System.err.println("Probably lost connection with DACPClient");
						}

					}
				}.start(); //Override the run method.

				
			}catch (IOException e){
				//e.printStackTrace();
			}
		}

		private ServerThread(Socket s) {
			this.SOCK = s;
		}

	}
}
