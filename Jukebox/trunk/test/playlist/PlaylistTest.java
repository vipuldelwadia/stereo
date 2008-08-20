package playlist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;


import org.junit.Test;

import playlist.Playlist;
import playlist.Track;




public class PlaylistTest {

	@Test
	public void createPlaylistEmpty() {
		new Playlist(new ArrayList<Track>());
	}
	
	@Test
	public void createPlaylist() {
        List<Track> tracks = new ArrayList<Track>();
//        tracks.add(new Track("Lithium0", "Nirvana", "", "Rock", 260));
//        tracks.add(new Track("Lithium1", "Nirvana", "", "Rock", 260));
//        tracks.add(new Track("Lithium2", "Nirvana", "", "Rock", 260));
//        tracks.add(new Track("Lithium3", "Nirvana", "", "Rock", 260));
//        tracks.add(new Track("Lithium4", "Nirvana", "", "Rock", 260));
//        tracks.add(new Track("Lithium5", "Nirvana", "", "Rock", 260));
//        tracks.add(new Track("Lithium6", "Nirvana", "", "Rock", 260));
//        tracks.add(new Track("Lithium7", "Nirvana", "", "Rock", 260));
		new Playlist(tracks);
	}
	
	@Test
	public void getTracks() {
		Playlist p = new Playlist(new ArrayList<Track>());
//		assertNotNull(p.getTracks());
		assertEquals(p.size(), 0);
		List<Track> tracks = new ArrayList<Track>();
//		Track t = new Track("Lithium", "Nirvana", "", "Rock", 260);
//		tracks.add(t);
		p = new Playlist(tracks);
	};
}
