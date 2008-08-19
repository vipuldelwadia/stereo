package music;

import java.io.IOException;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


import daap.DaapClient;


public class LackeyTest {
	
	@Test
	public void getTracksTest(){
		try {
			DaapClient client = new DaapClient("majoribanks.mcs.vuw.ac.nz", 3689);
			Lackey lackey = new Lackey();
			try {
				lackey.newConnection(client);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			List<Track> tracks = lackey.getAllTracks();
			assertTrue(tracks.size() > 1600);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	@Ignore("infinite loops ftw!!!")
	@Test
	public void handshakeTest(){
		Lackey lackey = new Lackey();
		while(true);
	}
	
}
