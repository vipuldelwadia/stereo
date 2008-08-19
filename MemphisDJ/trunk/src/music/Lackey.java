package music;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import daap.DaapClient;

public class Lackey {
	private Handshake hs;

	private List<DaapClient> clients;
	
	private Semaphore connectionQueue;

	public Lackey() {
		clients = new ArrayList<DaapClient>();
		connectionQueue = new Semaphore(1, true);

		try {
			hs = new Handshake(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("server socket failed to initialise");
			e.printStackTrace();
			System.exit(1);
		}

		new Thread(hs).start();
	}

	// public void removeTracks(Playlist plist){
	//		
	// }

	public List<Track> getAllTracks() {
		List<Track> tracks = new ArrayList<Track>();

		List<DaapClient> toRemove = new ArrayList<DaapClient>();
		for (DaapClient client : clients) {
			try {
				if (client.isAlive()) {
					tracks.addAll(client.getTrackList());
				} else {
					toRemove.add(client);
				}
			} catch (IOException e) {
				// swallow
			}
		}

		for (DaapClient c : toRemove) {
			clients.remove(c);
		}

		return tracks;
	}

	public void newConnection(DaapClient newClient) throws InterruptedException {
		if (newClient == null) 	return;
		connectionQueue.acquire();
		clients.add(newClient);
		DJ.getInstance().tracksAdded();
		connectionQueue.release();
	}


	private class Handshake implements Runnable {

		private ServerSocket connection;

		private final int DAAPPORT = 3689;

		private final int PORT = 8080;

		private BufferedReader netInput = null;

		private Lackey lackey;

		public Handshake(Lackey l) throws IOException {
			connection = new ServerSocket(PORT);
			this.lackey = l;
		}

		private DaapClient createConnection() throws IOException {
			Socket client = connection.accept();
			netInput = new BufferedReader(new InputStreamReader(client
					.getInputStream()));

			if (netInput.readLine().equals("GET /")) {
				// getInetAddress appends "/" to the start of a string
				String DAAPServer = client.getInetAddress().toString()
						.substring(1);
				client.close();
				return new DaapClient(DAAPServer, DAAPPORT);
			}

			client.close();
			// Fails if wrong message from client
			return null;
		}

		public void run() {
			while (true) {
				DaapClient client = null;
				try {
					client = createConnection();
					lackey.newConnection(client);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}


	
	public Playlist checkPlaylist (Playlist check){
		return check;
	}

}
