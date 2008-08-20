package test.playlist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import src.playlist.Playlist;
import src.playlist.Song;


public class PlaylistTest {

	@Test (expected = NullPointerException.class)
	public void createPlaylistNull() {
		new Playlist(null);
	}
	
	@Test
	public void createPlaylistEmpty() {
		new Playlist(new ArrayList<Song>());
	}
	
	@Test
	public void createPlaylist() {
        List<Song> tracks = new ArrayList<Song>();
        tracks.add(new Song("Lithium0", "Nirvana", "", "Rock", 260));
        tracks.add(new Song("Lithium1", "Nirvana", "", "Rock", 260));
        tracks.add(new Song("Lithium2", "Nirvana", "", "Rock", 260));
        tracks.add(new Song("Lithium3", "Nirvana", "", "Rock", 260));
        tracks.add(new Song("Lithium4", "Nirvana", "", "Rock", 260));
        tracks.add(new Song("Lithium5", "Nirvana", "", "Rock", 260));
        tracks.add(new Song("Lithium6", "Nirvana", "", "Rock", 260));
        tracks.add(new Song("Lithium7", "Nirvana", "", "Rock", 260));
		new Playlist(tracks);
	}
	
	@Test
	public void getTracks() {
		Playlist p = new Playlist(new ArrayList<Song>());
		assertNotNull(p.getSongs());
		assertEquals(p.size(), 0);
		List<Song> tracks = new ArrayList<Song>();
		Song t = new Song("Lithium", "Nirvana", "", "Rock", 260);
		tracks.add(t);
		p = new Playlist(tracks);
	};
}
