package playlist;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class TrackTest {

	private Track t;
	
	@Before
	public void createTrack() {
		t = new Track();
	}
	
	@Test
	public void nonNullFields() {
		assertNotNull(t.getAlbum());
		assertNotNull(t.getArtist());
		assertNotNull(t.getGenre());
		assertNotNull(t.getTitle());
		assertTrue(t.getTime() >= 0);
	}
	
}
