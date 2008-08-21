package music;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import playlist.Track;


public class PlaylistTest {
	
	List<Track> p;
	Track track;
	
	@Before
	public void setUp() throws Exception {
		p = new ArrayList<Track>();
		track = new Track(null, null);
	}
	
	@Test
	public void addTest(){
		assertTrue(p.add(track));
	}
	
	@Test
	public void removeTest(){
		p.add(track);
		assertTrue(p.remove(track));
	}
	
	

}
