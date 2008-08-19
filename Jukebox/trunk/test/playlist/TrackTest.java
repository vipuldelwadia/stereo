package test.playlist;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import src.playlist.Song;


public class TrackTest {

	private Song t;
	
	@Before
	public void createTrack() {
		t = new Song();
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
