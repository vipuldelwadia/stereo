/**
 * 
 */
package cli;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import music.DJ;
import playlist.Track;
import controller.ControllerInterface;
import daap.DAAPConstants;

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

	public List<Track> getPlaylist() {
		return dj.getPlaylist();
	}
	
	public void filter(String type, String criteria){
		Map<Integer,String> filter=new HashMap<Integer, String>();

		if (type.equalsIgnoreCase("artist")){
			filter.put(DAAPConstants.ARTIST, criteria);
		} else if (type.equalsIgnoreCase("album")){
			filter.put(DAAPConstants.ALBUM, criteria);
		}
		
		dj.setPlaylistWithFilter(filter);
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

	public void setPlaylist(List<Track> p) {
		dj.setPlaylist(p);
	}

	public void skipTrack() {
		dj.skip();
	}

	public void stop() {
		dj.stop();
	}
	
	public void status(){
		if(dj!=null && dj.getCurrentTrack()!=null)
		System.out.println(" #Current Track: "+dj.getCurrentTrack().toString()+" | Playback "+
				(dj.isPaused()? "Paused" : "Playing")
				);
		else
			System.out.println("No track loaded.");
	}
}
