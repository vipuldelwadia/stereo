package music;

import java.io.IOException;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


import daap.DaapClient;


public class LackeyTest {
	
	@Test
	public void getTracksTest(){
		try {
			DaapClient client = new DaapClient("majoribanks.mcs.vuw.ac.nz", 3689);
			Lackey lackey = new Lackey(null);
			lackey.newConnection(client);
			List<Track> tracks = lackey.getAllTracks();
			assertTrue(tracks.size() > 1600);
			
			tracks = lackey.getSomeTracks(10);
			assertEquals(10, tracks.size());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
}
