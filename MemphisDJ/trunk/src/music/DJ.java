package music;

import java.io.IOException;
import java.util.List;
import dacp.DACPServer;

public class DJ {

	private Lackey lackey;
	private Playlist playlist;
	private PlayerIF player;
	private Track current;
	
	private static final DJ instance = new DJ();
	
	public static DJ getInstance() {
		return instance;
	}
	
	private DJ (){
		lackey = new Lackey(this);
		playlist = new Playlist();
		//player = new Player();
		
		try {
			new DACPServer(3689);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Track> temp = lackey.getSomeTracks(10);
		for(Track t : temp){
			playlist.addTrack(t);
		}
		
		//current = playlist.poll();
		
		//player.setInputStream(current.getStream());
		play();
		
	}
	
	public void play(){
		//player.start();
		System.out.println("PLAY");
	}
	
	public void pause(){
		//player.pause();
		System.out.println("PAUSE");
	}
	
	public void stop(){
		//player.stop();
		System.out.println("NOOOOOOO");
	}
	
	public static void main(String[] args){
		DJ.getInstance();
	}
	
	
	
}
