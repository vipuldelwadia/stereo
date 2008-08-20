package music;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import playlist.Playlist;
import playlist.Track;


import daap.DAAPClient;


public class LackeyTest {
	
	@Test
	public void getTracksTest(){
		try {
			DAAPClient client = new DAAPClient("majoribanks.mcs.vuw.ac.nz", 3689);
			Lackey lackey = new Lackey(null);
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
		Lackey lackey = new Lackey(null);
		while(true);
	}
	
	@Test
	public void checkListTest(){
		Lackey l = new Lackey(null);
		Playlist p = new Playlist();
		try {
			p.addTrack(new Track(new HashMap<Integer, Object>(), new DAAPClient("majoribanks.mcs.vuw.ac.nz", 3689)));
			Playlist newP = l.checkPlaylist(p);
			assertEquals(0, newP.size());
		} catch (IOException e) {
			fail();
		}
	}
	
}
