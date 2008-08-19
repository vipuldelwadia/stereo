package playlist;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


public class PlaylistTest {

	@Test (expected = NullPointerException.class)
	public void createPlaylistNull() {
		new Playlist(null);
	}
	
	@Test
	public void createPlaylistEmpty() {
		new Playlist(new ArrayList<Track>());
	}
	
	@Test
	public void createPlaylist() {
		List<Track> tracks = new ArrayList<Track>();
		tracks.add(new Track());
		new Playlist(tracks);
	}
	
	@Test
	public void getTracks() {
		Playlist p = new Playlist(new ArrayList<Track>());
		assertNotNull(p.getPlaylist());
		assertEquals(p.getPlaylist().size(), 0);
		List<Track> tracks = new ArrayList<Track>();
		Track t = new Track();
		tracks.add(t);
		p = new Playlist(tracks);
		assertEquals(t, p.getPlaylist().get(0));
	};
}
