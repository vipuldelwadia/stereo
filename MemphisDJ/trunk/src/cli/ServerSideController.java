/**
 * 
 */
package cli;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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
		dj.setVolume(newVolume);
	}

	public List<Track> getPlaylist() {
		return dj.getPlaylist();
	}
	
	
	/**
	 * Returns a filtered playlist without replacing the current one.
	 * @param type
	 * @param criteria
	 */
	private Map<Integer,String> fillFilter(String type,String criteria,Map<Integer,String> playList){
		if (type.equalsIgnoreCase("artist"))
			playList.put(DAAPConstants.ARTIST, criteria);
		 else if (type.equalsIgnoreCase("album"))
			playList.put(DAAPConstants.ALBUM, criteria);
		
		return playList;
	}
	
	/**
	 * Do a filter on the playlist and replace the current playlist with the new filtered one.
	 */
	public void filter(String type, String criteria){
		Map<Integer,String> filter=new HashMap<Integer, String>();
		filter=fillFilter(type,criteria,filter);
		dj.setPlaylistWithFilter(filter);
	}
	
	/**
	 * Do a filter on the playlist and display it without replacing the current playlist with the new LIST of tracks.
	 */
	public void displayQuery(String type, String criteria){
		Map<Integer,String> filter=new HashMap<Integer, String>();
		filter=fillFilter(type,criteria,filter);
		for (Track currentTrack : dj.getPlaylistWithFilter(filter))
			System.out.print(currentTrack);
	}
	
	
	public int getVolume() {
		return (int)dj.getVolume();
	}

	public void pauseTrack() {
		dj.pause();
	}

	public void playTrack() {
		dj.unpause();
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
	
	public void recentlyPlayed(){
		Queue<Track> recent=dj.getRecentlyPlayedTracks();
		
		System.out.println("Recently played Music\n-------------------");
		while(!recent.isEmpty())
			System.out.println(recent.poll());
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
