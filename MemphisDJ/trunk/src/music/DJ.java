package music;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.List;
import java.util.Map;

import player.PlaybackListener;
import playlist.Playlist;
import playlist.Track;
import daap.DAAPClient;
import dacpserver.DACPServer;
import dacpserver.DACPServerListener;

public class DJ implements DACPServerListener, PlaybackListener{

	private Lackey lackey;

	private Playlist playlist;

	private int playlistSize = 10;

	private Player player;

	private Track current;

	private double currentVolume;
	private Queue<Track> recentlyPlayedTracks;
	private int recentlyPlayedTracksSize=30;
	private boolean paused;

//	private static final DJ instance = new DJ();

//	public static DJ getInstance() {
//	return instance;
//	}

	public DJ() {
		init();
		setPlaylist(new Playlist());
		player = new player.Player();
		player.addPlaybackListener(this);

		try {
			dacpserver.DACPServer s = new DACPServer(3689);
			s.addServerListener(this);
		} catch (IOException e) {
			System.out.println("DACP Server initialisation failed.");
			e.printStackTrace();
		}
	}

	public void init() {
		lackey = new Lackey(this);
	}

	// Stops player if library is empty
	private void fillPlaylist() {
		System.out.println("Attempting to fill playlist of size "
				+ getPlaylist().size());
		List<Track> lib = lackey.getAllTracks();
		if (lib != null && !lib.isEmpty()) {
			Collections.shuffle(lib);
			for (int i = 0; getPlaylist().size() < playlistSize
			&& i < lib.size(); i++) {
				Track t = lib.get(i);
				// unnecessary?
				if (t != null) {
					getPlaylist().addTrack(t);
				}
			}
			System.out.println("Playlist size: " + getPlaylist().size());

		} else {
			stop();
			System.out.println("Library empty: stopped playback.");
		}
	}
	/** This method will attempt to create a playlist so that people with the most will still
	 * get there music played evenly. It also makes sure that the tracks have not been recently played
	 *
	 */
	private void fillPlaylistEvenly(){
		Map<DAAPClient, Set<Track>> clientsTracks=libraryToOwnerMap();
		if (clientsTracks != null && !clientsTracks.isEmpty()){
			while (getPlaylist().size() < playlistSize){
				Set<DAAPClient> clients=clientsTracks.keySet();
				for(DAAPClient c: clients){
					Set<Track> clientTracks =clientsTracks.get(c);
					List<Track> orderedClientTracks=new ArrayList(clientTracks);
					Track t=orderedClientTracks.get((int)Math.random() % orderedClientTracks.size());
					if(!(recentlyPlayedTracks.contains(t))){	
						playlist.addTrack(t);
					}
				}
			}
		}
		else {
			stop();
			System.out.println("Library empty: stopped playback.");
		}
	}
	private Map<DAAPClient, Set<Track>> libraryToOwnerMap(){
		List<Track> lib = lackey.getAllTracks();
		Map<DAAPClient, Set<Track>> clientsTracks=new HashMap<DAAPClient, Set<Track>>();
		for(Track t:lib){
			if(!(clientsTracks.containsKey(t.getParent())))clientsTracks.put(t.getParent(), new HashSet<Track>());
			Set<Track> clientTracks=clientsTracks.get(t.getParent());
			clientTracks.add(t);
		}	
		return clientsTracks;
	}
	/**
	 * 
	 * @param c
	 *            A map of filter criterias. The key will be the search category
	 *            such as 'artist' or 'album, the value associated with the key
	 *            is the word the filter will try to match with.
	 * @return
	 */
	public void setTracksFiltered(Map<Integer, String> c){

		Playlist returned = new Playlist();
		List<Track> allTracks = lackey.getAllTracks();

		//Search through every criteria for potential matches
		for(int i=0;i<allTracks.size();i++){

			Track currentTrack=allTracks.get(i);
			boolean fitCrit=true;

			for(int s : c.keySet()){
				if (!c.get(s).equals((String)currentTrack.getTag(s)))	 {
					fitCrit=false;
					break;
				}
			}

			if (fitCrit)returned.addTrack(currentTrack);

		}
		stop();
		playlist=returned;



		if (!playlist.isEmpty()) {
			current = playlist.poll();
			System.out.println("Polled playlist.");
			recentlyPlayedTracks.add(current);
			if(recentlyPlayedTracks.size()>recentlyPlayedTracksSize)
			recentlyPlayedTracks.poll();
			try {
				player.setInputStream(current.getStream());
			} catch (IOException e) {
				e.printStackTrace();
			}


		}
	}
	public void play() {
		player.start();
		paused=false;
	}

	public void pause(){
		player.pause();
		paused=true;
	}

	public void stop(){
		player.stop();
		paused=true;
	}

	public void setVolume(double volume) {
		currentVolume = volume;
		URL url = DJ.class.getResource("setvolume.sh");

		try {
			System.out.println("setting volume to " + volume);
			Process p = Runtime.getRuntime().exec(
					"bash " + url.getFile() + " " + (int) volume);
			// for (Scanner sc = new Scanner(p.getErrorStream());
			// sc.hasNextLine();) {
			// System.out.println(sc.nextLine());
			// }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public double getVolume(){
		return currentVolume;
	}

	public void skip() {
		player.stop();
		playbackFinished();
	}


	//Starts the player if playlist was empty
	public void tracksAdded(){

		System.out.println("tracks added");

		if(getPlaylist().isEmpty()){

			fillPlaylist();
			if (getPlaylist().isEmpty()) return;

			current = getPlaylist().poll();
			recentlyPlayedTracks.add(current);
			if(recentlyPlayedTracks.size()>recentlyPlayedTracksSize)
				recentlyPlayedTracks.poll();
			System.out.println("Polled playlist.");
			try {
				player.setInputStream(current.getStream());
			} catch (IOException e) {
				System.out.println("Failed to send stream to player.");
				e.printStackTrace();
			}
		}
		if (getPlaylist().size() < playlistSize){
			fillPlaylist();	
		}

	}


	public void libraryChanged(){
		lackey.checkPlaylist(getPlaylist());
		if (getPlaylist().size() < playlistSize){
			fillPlaylist();	
		}
		System.out.println("Library changed: playlist updated");
	}

	public void playbackFinished() {
		InputStream stream = null;
		System.out.println("Playback finished.");
		System.out.println("Size of Playlist: " + getPlaylist().size());
		while (stream == null) {
			try {
				if (getPlaylist().isEmpty()) fillPlaylist();
				if (getPlaylist().isEmpty()) return;

				current = getPlaylist().poll();
				recentlyPlayedTracks.add(current);
				if(recentlyPlayedTracks.size()>recentlyPlayedTracksSize)
					recentlyPlayedTracks.poll();
				if(((String)current.getTag(Track.NAME)).endsWith(".ogg")||((String)current.getTag(Track.NAME)).endsWith(".wav")){
					if (((String) current.getTag(Track.NAME)).endsWith(".ogg")
							|| ((String) current.getTag(Track.NAME))
							.endsWith(".wav")) {
						continue;
					}
					System.out.println("Polled playlist.");
					stream = current.getStream();
					fillPlaylist();
				}
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
			player.setInputStream(stream);
		}
	}
	public void playbackStarted() {
		// TODO Auto-generated method stub

	}

	public void setPlaylist(Playlist playlist) {
		this.playlist = playlist;
	}

	public Playlist getPlaylist() {
		return playlist;
	}

	public static void main(String[] args){

		DJ a = new DJ();

	}

}
