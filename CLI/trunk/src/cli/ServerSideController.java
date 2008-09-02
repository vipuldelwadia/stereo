package cli;

/**
 * 
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import music.DJ;
import playlist.Track;
import clinterface.CLI;
import controller.ControllerInterface;
import daap.DAAPConstants;

/**
 * @author abrahajoav
 *
 */
public class ServerSideController implements ControllerInterface {
	
	private DJ dj;
	
	
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
	public void createPlaylistWithFilter(String type, String criteria){
		Map<Integer,String> filter=new HashMap<Integer, String>();
		filter=fillFilter(type,criteria,filter);
		dj.setPlaylistWithFilter(filter);
	}
	
	/**
	 * Do a filter on the playlist and display it without replacing the current playlist with the new LIST of tracks.
	 */
	public void queryLibrary(String type, String criteria){
		Map<Integer,String> filter=new HashMap<Integer, String>();
		filter=fillFilter(type,criteria,filter);
		for (Track currentTrack : dj.getPlaylistWithFilter(filter))
			System.out.print(currentTrack);
	}
	
	/**
	 * Append a new filtered playlist on the bottom of the old playlist.
	 * @param type 
	 * @param criteria
	 * @param oldPL
	 * @return
	 */
	public void append(String type, String criteria){
		Map<Integer, String> filter = new HashMap<Integer, String>();
		filter = fillFilter(type, criteria, filter);

		dj.getPlaylist().addAll(dj.getPlaylistWithFilter(filter));
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
	
	public void displayLibrary(){
		System.out.println("The Library Contents\n-------------------");
		for(Track currentTrack : dj.getLibrary())
			System.out.print(currentTrack.toString());
	}
	
	public void queryRecentlyPlayed(){
		List<Track> recent=dj.getRecentlyPlayedTracks();
		
		System.out.println("Recently played Music\n-------------------");
		for (Track t : recent){
			System.out.println(t);
		}
	}
	
	public void status(){
		if(dj!=null && dj.getCurrentTrack()!=null)
		System.out.println(" #Current Track: "+dj.getCurrentTrack().toString()+" | Playback "+
				(dj.isPaused()? "Paused" : "Playing")
				);
		else
			System.out.println("No track loaded.");
	}
	
	
    
    public static void main(String[] args) {
    	if (args.length == 0) {
    		new CLI(new ServerSideController());
    	}
    	else {
    		String combinedArgs = "";
    		for(String s : args) {
    			combinedArgs += " " + s;
    		}
    		combinedArgs = combinedArgs.trim();
    		System.out.println(combinedArgs);
    		new CLI(new ServerSideController(), combinedArgs);
    	}
    }
    
}
