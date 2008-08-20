package music;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Semaphore;

import daap.DaapClient;
import daap.DaapClient.ClientExpiredException;

public class Lackey {
	private Handshake hs;

	private Semaphore connectionQueue;
	
	private Map<DaapClient, Set<Track>> library;
	
	private final DJ dj;

	public Lackey(DJ dj) {
		library = new HashMap<DaapClient, Set<Track>>();
		connectionQueue = new Semaphore(1, true);
		this.dj = dj;
		
		try {
			hs = new Handshake(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("server socket failed to initialise");
			e.printStackTrace();
			System.exit(1);
		}

		new Thread(hs).start();
		new Thread(new ServerPoller()).start();
	}

	// public void removeTracks(Playlist plist){
	//		
	// }

	public Playlist checkPlaylist (Playlist check){
		return check;
	}

	public List<Track> getAllTracks() {
		List<Track> tracks = new ArrayList<Track>();
		for(Set<Track> trackSet:library.values()){
			tracks.addAll(trackSet);
		}
		return tracks;
	}


	public void newConnection(DaapClient newClient) throws InterruptedException, IOException {
		if (newClient == null) 	return;
		connectionQueue.acquire();
		System.out.println("Permission acquired");
		Set<Track> tracks = new HashSet<Track>(newClient.getTrackList());
		System.out.println("Tracks retrieved.");
		library.put(newClient, tracks);
		System.out.println("Tracks and client added to library");
		//DJ dj = DJ.getInstance();
		System.out.println("Got DJ");
		dj.tracksAdded();
		System.out.println("DJ Informed");
		connectionQueue.release();
		System.out.println("Permission released");
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
					System.out.println("Lackey accepted connection.");
					lackey.newConnection(client);
					System.out.println("Lackey created connection.");
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class ServerPoller implements Runnable{
		
		public void run(){
			while(true){
				List<DaapClient> expired = new ArrayList<DaapClient>();
				
				for(DaapClient client:library.keySet()){
					try {
						if(client.isUpdated()){
							library.put(client, new HashSet<Track>(client.getTrackList()));
						}
					} catch (ClientExpiredException e) {
						expired.add(client);
					}
				}
			
				if(expired.size() > 0){
					for(DaapClient c:expired){
						library.remove(c);
					}
					dj.tracksRemoved();
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					//swallow
				}
			}
		}
	}

}
