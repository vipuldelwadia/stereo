import java.util.ArrayList;
import java.util.List;

/** A class representing a playlist of songs 
 *
 * 
 * @author coxdyla
 *
 */
public class Playlist {
private List<Song> playlist;
Song curSong;
public Playlist(){
	playlist=new ArrayList<Song>();
	curSong=playlist.get(0);
}
public Song getCurSong() {
	return curSong;
}
public List<Song> getPlaylist(int num) {
	return playlist.subList(0, num);
}
}
