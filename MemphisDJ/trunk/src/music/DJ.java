package music;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import player.PlaybackListener;
import sun.misc.VM;
import dacpserver.DACPServer;
import dacpserver.DACPServerListener;

public class DJ implements DACPServerListener, PlaybackListener{

	private Lackey lackey;
	private Playlist playlist;
	private int playlistSize = 10;
	private Player player;
	private Track current;

//	private static final DJ instance = new DJ();

//	public static DJ getInstance() {
//	return instance;
//	}

	private DJ (){
		lackey = new Lackey(this);
		playlist = new Playlist();
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
		System.out.println("Attempting to fill playlist of size " + playlist.size());
		List<Track> lib = lackey.getAllTracks();
		if (lib != null && !lib.isEmpty()){
			Collections.shuffle(lib);
			for (int i = 0; playlist.size() < playlistSize && i < lib.size(); i++){
				Track t = lib.get(i);
				//unnecessary?
				if (t != null){
					playlist.addTrack(t);
				}
			}
			System.out.println("Playlist size: " + playlist.size());
			
		}	
		else {
			stop();
			System.out.println("Library empty: stopped playback.");
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
		
		URL url = DJ.class.getResource("setvolume.sh");
		
		try {
			Process p = Runtime.getRuntime().exec("bash " + url.getFile() + " " + (int)volume);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public void skip() {
		// TODO Auto-generated method stub
		playbackFinished();

	}


	//Starts the player if playlist was empty
	public void tracksAdded(){

		System.out.println("tracks added");

		if(playlist.isEmpty()){
			
			fillPlaylist();
			if (playlist.isEmpty()) return;
			
			current = playlist.poll();
			System.out.println("Polled playlist.");
			try {
				player.setInputStream(current.getStream());
			} catch (IOException e) {
				System.out.println("Failed to send stream to player.");
				e.printStackTrace();
			}
		}
		if (playlist.size() < playlistSize){
			fillPlaylist();	
		}

	}


	public void tracksRemoved(){
		playlist = lackey.checkPlaylist(playlist);
		if (playlist.size() < playlistSize){

			fillPlaylist();	
		}
		System.out.println("Tracks removed.");
	}

	public void playbackFinished() {
		InputStream stream = null;
		System.out.println("Playback finished.");
		System.out.println("Size of Playlist: " + playlist.size());
		while (stream == null) {
			try {
				if (playlist.isEmpty()) fillPlaylist();
				if (playlist.isEmpty()) return;
				
				current = playlist.poll();
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

	public static void main(String[] args){

		new DJ().setVolume(100);
	}
	
}
