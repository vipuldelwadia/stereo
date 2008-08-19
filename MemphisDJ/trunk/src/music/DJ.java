package music;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import server.DACPServer;

public class DJ {

	private Lackey lackey;
	private Playlist playlist;
	private int playlistSize = 10;
	private Player player;
	private Track current;
	
	private static final DJ instance = new DJ();
	
	public static DJ getInstance() {
		return instance;
	}
	
	private DJ (){
		lackey = new Lackey();
		playlist = new Playlist();
		player = new player.Player();
		
		try {
			new DACPServer(3689);
		} catch (IOException e) {
			System.out.println("DACP Server initialisation failed.");
			e.printStackTrace();
		}
		
		fillPlaylist();	
		
		current = playlist.poll();
		
		try {
			player.setInputStream(current.getStream());
		} catch (IOException e) {
			System.out.println("Failed to send stream to player.");
			e.printStackTrace();
		}
		play();
		
	}
	
	public void fillPlaylist(){
		List<Track> lib = lackey.getAllTracks();
		if (lib != null){
			Collections.shuffle(lib);
			for (int i = 0; i < playlistSize && i < lib.size(); i++){
				Track t = lib.get(i);
				//unnecessary?
				if (t != null){
					playlist.addTrack(t);
				}
			}
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
	
	public static void main(String[] args){
		DJ.getInstance();
	}
	
	public void tracksAdded(){
		
		
	}
	public void tracksRemoved(){
		playlist = lackey.checkPlaylist(playlist);
		if (playlist.size() < playlistSize){
			
		fillPlaylist();	
		}
		
	}
	
}
