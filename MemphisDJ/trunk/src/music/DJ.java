package music;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import player.PlaybackListener;
import playlist.Track;
import daap.DAAPClient;
import daap.DAAPConstants;

//public class DJ implements DACPServerListener, PlaybackListener{
public class DJ implements PlaybackListener{

	private Lackey lackey;

	private List<Track> playlist;

	private int playlistSize = 10;

	private Player player;

	private Track current;

	private double currentVolume;
	private Queue<Track> recentlyPlayedTracks=new LinkedList<Track>();
	private int recentlyPlayedTracksSize=30;
	private boolean paused;

	
	public DJ() {
		init();
		playlist = new ArrayList<Track>();
		player = new player.Player();
		player.addPlaybackListener(this);

		try {
			DACPDJ s = new DACPDJ(3689, this);
			//TODO
			//s.addServerListener(this);
		} catch (IOException e) {
			System.out.println("DACP Server initialisation failed.");
			e.printStackTrace();
		}
	}

	
	public void init() {
		lackey = new Lackey(this);
	}

	private void fillPlaylist() {
		System.out.println("Attempting to fill playlist of size " + getPlaylist().size());
		List<Track> lib = lackey.getAllTracks();
		if (lib != null && !lib.isEmpty()) {
			Collections.shuffle(lib);
			for (int i = 0; getPlaylist().size() < playlistSize && i < lib.size(); i++) {
				Track t = lib.get(i);
				// unnecessary?
				if (t != null) {
					getPlaylist().add(t);
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
						playlist.add(t);
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
	public List<Track> getPlaylistWithFilter(Map<Integer, String> c){
		List<Track> returned = new ArrayList<Track>();
		List<Track> allTracks = lackey.getAllTracks();

		System.out.println("DAAPConstants.ALBUM="+DAAPConstants.ARTIST+" "+c);
		
		
		//Search through every criteria for potential matches
		for(int i=0; i<allTracks.size(); i++){

			Track currentTrack=allTracks.get(i);
			boolean fitCrit=true;

			for(int s : c.keySet()){
				
				if (!c.get(s).equalsIgnoreCase((String)currentTrack.getTag(s)))	{
					fitCrit=false;
					break;
				}
			}

			if (fitCrit)returned.add(currentTrack);

		}
		return returned;
	}
	
	/**
	 * Takes a set of Track identifier (e.g Track.ALBUM) - value pairs
	 * which will select from all songs the songs which meet
	 * the criteria and then fill the playlist.
	 * @param c
	 */
	public void setPlaylistWithFilter(Map<Integer, String> c){
		stop();
		playlist=getPlaylistWithFilter(c);
/*
		if (!playlist.isEmpty()) {
			current = playlist.remove(0);
			System.out.println("Polled playlist.");

			recentlyPlayedTracks.add(current);
			if(recentlyPlayedTracks.size() > recentlyPlayedTracksSize)
				recentlyPlayedTracks.poll();

			try {
				player.setInputStream(current.getStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		*/
		start();
	}
	
	public void appendTracks(Map<Integer, String> c){
		List<Track> toAppend = getPlaylistWithFilter(c);
		
		playlist.addAll(toAppend);
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
			e.printStackTrace();
		}


	}


	/*
	 * Lackey calls when 
	 */
	public void tracksAdded(){
		System.out.println("tracks added");

		if(getPlaylist().isEmpty()){

			fillPlaylist();
			start();
		}
		
		if (getPlaylist().size() < playlistSize){
			fillPlaylist();	
		}

	}

	/*
	 * Lackey calls this when the library has changed (i.e DAAP server dropped).
	 * Validates the current playlist.
	 */
	public void libraryChanged(){
		lackey.checkPlaylist(playlist);
		if (getPlaylist().size() < playlistSize){
			fillPlaylist();	
		}
		System.out.println("Library changed: playlist updated");
	}
	

	/*
	 * Modify DJ state
	 */
	public void unpause() {
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

	public void skip() {
		player.stop();
		playbackFinished();
	}
	
	public void start(){
		if (getPlaylist().isEmpty()) return;

		current = getPlaylist().remove(0);
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
	
	public void setPlaylist(List<Track> playlist) {
		this.playlist = playlist;
	}

	
	/*
	 * Info about DJ state
	 */
	public double getVolume(){
		return currentVolume;
	}
	
	public boolean isPaused(){
		return paused;
	}
	
	public List<Track> getPlaylist() {
		return playlist;
	}

	public Track getCurrentTrack(){
		return current;
	}
	
	public Queue<Track> getRecentlyPlayedTracks() {
		return recentlyPlayedTracks;
	}
	
	//TODO should this return a List or a playlist?
	public List<Track> getLibrary(){
		return Collections.unmodifiableList(lackey.getAllTracks());
	}

	
	/*
	 * Methods from PlaybackListener
	 * 
	 */
	public void playbackFinished() {
		InputStream stream = null;
		System.out.println("Playback finished.");
		System.out.println("Size of Playlist: " + playlist.size());
		while (stream == null) {
			try {
				if (playlist.isEmpty()) fillPlaylist();
				if (playlist.isEmpty()) return;

				current = playlist.remove(0); 
				recentlyPlayedTracks.add(current);
				if(recentlyPlayedTracks.size() > recentlyPlayedTracksSize)
					recentlyPlayedTracks.poll();


				if (((String) current.getTag(DAAPConstants.NAME)).endsWith(".ogg")
						|| ((String) current.getTag(DAAPConstants.NAME))
						.endsWith(".wav")) {
					continue;
				}
				System.out.println("Polled playlist.");
				stream = current.getStream();
				fillPlaylist();

			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}



		player.setInputStream(stream);
	}
	
	public void playbackStarted() {
		System.out.println("Playback started");

	}

	
	
	public static void main(String[] args){
		DJ a = new DJ();
	}
}
