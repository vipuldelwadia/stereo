package music;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;


import player.PlaybackListener;
import playlist.Playlist;
import playlist.Track;
import sun.misc.VM;
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
	

//	private static final DJ instance = new DJ();

//	public static DJ getInstance() {
//	return instance;
//	}

	public DJ (){
		lackey = new Lackey(this);
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

	// Stops player if library is empty
	public void fillPlaylist(){
		System.out.println("Attempting to fill playlist of size " + getPlaylist().size());
		List<Track> lib = lackey.getAllTracks();
		if (lib != null && !lib.isEmpty()){
			Collections.shuffle(lib);
			for (int i = 0; getPlaylist().size() < playlistSize && i < lib.size(); i++){
				Track t = lib.get(i);
				//unnecessary?
				if (t != null){
					getPlaylist().addTrack(t);
				}
			}
			System.out.println("Playlist size: " + getPlaylist().size());

		}	
		else {
			stop();
			System.out.println("Library empty: stopped playback.");
		}
	}
	public void fillPlaylistEvenly(){
		System.out.println("Attempting to fill playlist of size " + getPlaylist().size());
		Map<DAAPClient, Set<Track>> lib=lackey.getLibrary();
		Set<DAAPClient> clients= lib.keySet();
		if (lib != null && !lib.isEmpty()){
			while(getPlaylist().size() < playlistSize){
				for(DAAPClient d : clients){
					Set<Track> tracks=lib.get(d);
					Track random=tracks.iterator().next();
					if (random != null){
						getPlaylist().addTrack(random);
				}

				}
			}
			System.out.println("Playlist size: " + getPlaylist().size());
			
		}	
		else {
			stop();
			System.out.println("Library empty: stopped playback.");
		}
	}


	/**
	 * 
	 * @param c A map of filter criterias. The key will be the search category such as 'artist' or 'album, the value associated with the key is the word the filter will try to match with. 
	 * @return
	 */
	public void getTracksFiltered(Map<Integer, String> c){

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
		
		current = playlist.poll();
		System.out.println("Polled playlist.");

			try {
				player.setInputStream(current.getStream());
			} catch (IOException e) {
				e.printStackTrace();
			}

		
	}


	public void play(){
		player.start();
	}

	public void pause(){
		player.pause();
	}

	public void stop(){
		player.stop();
	}

	public void setVolume(double volume) {
		currentVolume = volume;
		URL url = DJ.class.getResource("setvolume.sh");

		try {
			Process p = Runtime.getRuntime().exec("bash " + url.getFile() + " " + (int)volume);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public double getVolume(){
		return currentVolume;
	}

	public void skip() {
		// TODO Auto-generated method stub
		playbackFinished();

	}


	//Starts the player if playlist was empty
	public void tracksAdded(){

		System.out.println("tracks added");

		if(getPlaylist().isEmpty()){

			fillPlaylist();
			if (getPlaylist().isEmpty()) return;

			current = getPlaylist().poll();
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
		setPlaylist(lackey.checkPlaylist(getPlaylist()));
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
		// TODO Auto-generated method stub

	}

	private void setPlaylist(Playlist playlist) {
		this.playlist = playlist;
	}

	private Playlist getPlaylist() {
		return playlist;
	}

	public static void main(String[] args){

		DJ a = new DJ();

	}

}
