package src.player;

import src.playlist.Playlist;
import music.DJ;



public class DummyController {
	
	private DJ dj;
	
	public DummyController(){
		DJ.getInstance();
	}

	public void changeVolume(int newVolume) {
		dj.setVolume(newVolume);

	}

	public Playlist getPlaylist() {
		return null;
	}

	public int getVolume() {
		return 0;
	}

	public boolean isValidController() {
		return true;
	}

	public void pauseTrack() {
		dj.pause();
	}

	public void playTrack() {
		dj.play();
	}

	public void skipTrack() {
		dj.skip();

	}
}
