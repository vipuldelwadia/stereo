/**
 * 
 */
package cli;

import music.DJ;
import controller.ControllerInterface;
import playlist.Playlist;

/**
 * @author abrahajoav
 *
 */
public class ServerSideController implements ControllerInterface {
	
	private DJ dj;

	/**
	 * 
	 */
	
	public ServerSideController() {
		dj = new DJ();
	}

	public void changeVolume(int newVolume) {
		dj.setVolume((double)newVolume);
	}

	public Playlist getPlaylist() {
		return dj.getPlaylist();
	}

	public int getVolume() {
		return (int)dj.getVolume();
	}

	public void pauseTrack() {
		dj.pause();
	}

	public void playTrack() {
		dj.play();
	}

	public void setPlaylist(Playlist p) {
		dj.setPlaylist(p);
	}

	public void skipTrack() {
		dj.skip();
	}

	public void stop() {
		dj.stop();
	}

}
