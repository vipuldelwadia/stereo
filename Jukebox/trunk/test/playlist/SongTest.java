package playlist;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import jukebox.Song;

import org.junit.Before;
import org.junit.Test;



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
