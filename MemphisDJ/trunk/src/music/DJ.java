package music;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import player.PlaybackListener;
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
		List<Track> lib = lackey.getAllTracks();
		if (lib != null && !lib.isEmpty()){
			Collections.shuffle(lib);
			for (int i = 0; i < playlistSize && i < lib.size(); i++){
				Track t = lib.get(i);
				//unnecessary?
				if (t != null){
					playlist.addTrack(t);
				}
			}
		}
		else {
			stop();
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
		// TODO Auto-generated method stub

	}

	public void skip() {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args){

		new DJ();
	}

	//Starts the player if playlist was empty
	public void tracksAdded(){

		System.out.println("tracks added");

		if(playlist.isEmpty()){
			
			fillPlaylist();
			if (playlist.isEmpty()) return;
			
			current = playlist.poll();
			try {
				player.setInputStream(current.getStream());
			} catch (IOException e) {
				System.out.println("Failed to send stream to player.");
				e.printStackTrace();
			}
		}
		else if (playlist.size() < playlistSize){
			fillPlaylist();	
		}

	}


	public void tracksRemoved(){
		System.out.println("tracks were removed");
		playlist = lackey.checkPlaylist(playlist);
		if (playlist.size() < playlistSize){

			fillPlaylist();	
		}

	}

	public void playbackFinished() {
		InputStream stream = null;
		while (stream == null) {
			try {
				if (playlist.isEmpty()) fillPlaylist();
				if (playlist.isEmpty()) return;
				
				current = playlist.poll();
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

}
