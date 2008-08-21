package music;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import playlist.Track;

import daap.DAAPClient;
import daap.DAAPClient.ClientExpiredException;

public class Lackey {
	
	private final Map<DAAPClient, Set<Track>> library;
	private final DJ dj;
	
	private Set<Track> tracks = new HashSet<Track>();
	private List<Track> tracklist = new ArrayList<Track>();
	
	
	public Lackey(DJ dj) {
		
		library = new HashMap<DAAPClient, Set<Track>>();
		this.dj = dj;
		
		Handshake hs;
		try {
			hs = new Handshake(this);
			hs.start();
		} catch (IOException e) {
			System.out.println("server socket failed to initialise");
			e.printStackTrace();
			System.exit(1);
		}
		
		new WorkerThread().start();
	}
	
	public void checkPlaylist(List<Track> check) {
		for (Iterator<Track> iter = check.iterator(); iter.hasNext();) {
			Track element = iter.next();
			if(!tracks.contains(element)){
				iter.remove();
			}
		}
	}
	
	public List<Track> getAllTracks() {
		return tracklist;
	}
	
	public void newConnection(DAAPClient client) {
		worklist.add(new NewConnectionJob(client));
	}
	
	
	
	//internal stuff
	private Queue<Job> worklist = new LinkedList<Job>();
	
	private class WorkerThread extends Thread {
		
		public void run() {
			while (true) {
				
				worklist.offer(new ServerPollJob());
				
				while (!worklist.isEmpty()) {
					Job job = worklist.poll();
					//System.out.println(job.getClass());
					job.run();
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					//swallow
				}
			}
		}
		
	}
	
	private interface Job {
		public void run();
	}
	
	private class NewConnectionJob implements Job {
		private final DAAPClient client;
		public NewConnectionJob(DAAPClient client) {
			this.client = client;
		}
		public void run() {
			if (client == null) 	return;
			Set<Track> tracks = new HashSet<Track>(client.getTrackList());
			System.out.println("Tracks retrieved: " + tracks.size());
			library.put(client, tracks);
			worklist.offer(new RebuildTracklistJob());
			System.out.println("Tracks and client added to library");
			worklist.offer(new TracksAddedJob());
		}
	}

	private class TracksAddedJob implements Job {
		public void run() {
			dj.tracksAdded();
			System.out.println("DJ Informed");
		}
	}
	
	private class LibraryChangedJob implements Job {
		public void run() {
			dj.libraryChanged();
			System.out.println("DJ Informed");
		}
	}
	
	private class RebuildTracklistJob implements Job {
		public void run() {
			Set<Track> newTracks = new HashSet<Track>();
			List<Track> newTracklist = new ArrayList<Track>();
			for(Set<Track> trackSet:library.values()){
				newTracks.addAll(trackSet);
				newTracklist.addAll(trackSet);
			}
			tracks = newTracks;
			tracklist = newTracklist;
		}
	}

	private class Handshake extends Thread {

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
				} catch (Exception e) {
					System.err.println("Error creating connection");
					//e.printStackTrace();
				}
			}
		}
	}
	
	private class ServerPollJob implements Job {
		
		public void run(){
			List<DAAPClient> expired = new ArrayList<DAAPClient>();

			for(DAAPClient client:library.keySet()){
				try {
					if(client.isUpdated()){
						System.out.println("client changed");
						library.put(client, new HashSet<Track>(client.getTrackList()));
						worklist.offer(new RebuildTracklistJob());
						worklist.offer(new LibraryChangedJob());
					}
				} catch (ClientExpiredException e) {
					expired.add(client);
				}
			}

			if(expired.size() > 0){
				for(DAAPClient c:expired){
					library.remove(c);
				}
				worklist.offer(new RebuildTracklistJob());
				worklist.offer(new LibraryChangedJob());
			}
		}
	}
}
