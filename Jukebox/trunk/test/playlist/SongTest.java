package test.playlist;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import src.playlist.Song;


public class SongTest {

	private Song s;
	
	@Before
	public void createTrack() {
		s = new Song("Lithium0", "Nirvana", "", "Rock", 260);
	}
	
	@Test
	public void nonNullFields() {
		assertNotNull(s.getAlbum());
		assertNotNull(s.getArtist());
		assertNotNull(s.getGenre());
		assertNotNull(s.getTitle());
		assertTrue(s.getSeconds() >= 0);
	}
	
}