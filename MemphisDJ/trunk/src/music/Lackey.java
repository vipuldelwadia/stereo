package music;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Semaphore;

import daap.DAAPClient;
import daap.DAAPClient.ClientExpiredException;

public class Lackey {
	private Handshake hs;

	private Semaphore connectionQueue;
	
	private Map<DAAPClient, Set<Track>> library;
	
	private final DJ dj;

	public Lackey(DJ dj) {
		library = new HashMap<DAAPClient, Set<Track>>();
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
		Set<Track> allTracks = new HashSet<Track>();
		for(Set<Track> trackSet:library.values()){
			allTracks.addAll(trackSet);
		}
		
		for (Iterator<Track> iter = check.iterator(); iter.hasNext();) {
			Track element = iter.next();
			if(!allTracks.contains(element)){
				iter.remove();
			}
		}
		return check;
	}

	public List<Track> getAllTracks() {
		List<Track> tracks = new ArrayList<Track>();
		for(Set<Track> trackSet:library.values()){
			tracks.addAll(trackSet);
		}
		return tracks;
	}


	public void newConnection(DAAPClient newClient) throws InterruptedException, IOException {
		if (newClient == null) 	return;
		connectionQueue.acquire();
		Set<Track> tracks = new HashSet<Track>(newClient.getTrackList());
		System.out.println("Tracks retrieved.");
		library.put(newClient, tracks);
		System.out.println("Tracks and client added to library");
		dj.tracksAdded();
		System.out.println("DJ Informed");
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

		private DAAPClient createConnection() throws IOException {
			Socket client = connection.accept();
			netInput = new BufferedReader(new InputStreamReader(client
					.getInputStream()));

			if (netInput.readLine().equals("GET /")) {
				// getInetAddress appends "/" to the start of a string
				String DAAPServer = client.getInetAddress().toString()
						.substring(1);
				client.close();
				return new DAAPClient(DAAPServer, DAAPPORT);
			}

			client.close();
			// Fails if wrong message from client
			return null;
		}

		public void run() {
			while (true) {
				DAAPClient client = null;
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
				List<DAAPClient> expired = new ArrayList<DAAPClient>();
				
				for(DAAPClient client:library.keySet()){
					try {
						if(client.isUpdated()){
							System.out.println("client changed");
							library.put(client, new HashSet<Track>(client.getTrackList()));
							dj.libraryChanged();
						}
					} catch (ClientExpiredException e) {
						expired.add(client);
					}
				}
			
				if(expired.size() > 0){
					for(DAAPClient c:expired){
						library.remove(c);
					}
					dj.libraryChanged();
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
