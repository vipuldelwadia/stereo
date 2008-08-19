package music;

import java.util.ArrayList;
import java.util.Iterator;

public class Playlist implements Iterable<Track>{
	
	private ArrayList<Track> tracks;

	public Playlist(){
		tracks = new ArrayList<Track>();
	}
	
	public boolean addTrack(Track t){
		return tracks.add(t);
	}
	
	public boolean removeTrack(Track t){
		return tracks.remove(t);
	}

	public Iterator<Track> iterator(){
		return tracks.iterator();
	}
	
	public boolean contains(Track t){
		if(tracks.contains(t)){
			return true;
		}
		return false;
	}
	
	public Track peek(){
		return tracks.get(0);
	}
	
	public Track poll(){
		Track t = tracks.get(0);
		removeTrack(t);
		return t;
	}
	
}
