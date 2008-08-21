package playlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;


public class Playlist implements Iterable<Track> {
	
	private List<Track> tracks;

	public Playlist(){
		tracks = new ArrayList<Track>();
	}
	
	public Playlist(List<Track> tracks) {
		this.tracks = tracks;
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
	
	public int size(){
		return tracks.size();
	}
	
	public Track peek(){
		return tracks.get(0);
	}
	
	public Track poll(){
		Track t = tracks.get(0);
		removeTrack(t);
		return t;
	}

	public boolean isEmpty() {
		return tracks.isEmpty();
	}

	public String toString(){
		String listing = "";
		for (Track t: tracks){
			listing +=t.toString() + "\n";
		}
		
		return listing;
	}
	
}
