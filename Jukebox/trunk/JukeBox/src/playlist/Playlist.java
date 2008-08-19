package playlist;

import java.util.Collections;
import java.util.List;

public class Playlist {
	private final List<Track> playlist;
	public Playlist(List<Track> tracks){
		if (tracks == null) throw new NullPointerException("tracks cannot be null");
		playlist=Collections.unmodifiableList(tracks);
	}
	public void sortField(){
		
	}
	public List<Track> getPlaylist() {
		return playlist;
	}
	
}
