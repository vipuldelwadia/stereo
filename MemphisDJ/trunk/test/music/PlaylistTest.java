package music;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class PlaylistTest {
	
	Playlist p;
	Track track;
	
	@Before
	public void setUp() throws Exception {
		p = new Playlist();
		track = new Track(null, null);
	}
	
	@Test
	public void addTest(){
		assertTrue(p.addTrack(track));
	}
	
	@Test
	public void removeTest(){
		assertTrue(p.removeTrack(track));
	}

}
