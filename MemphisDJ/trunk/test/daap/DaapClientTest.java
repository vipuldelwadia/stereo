package daap;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import music.Track;

import org.junit.Test;


public class DaapClientTest {
	
	@Test
	public void simpleTest(){
		try {
			new DaapClient("majoribanks.mcs.vuw.ac.nz", 3689);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void playTest(){
		try {
			DaapClient client =  new DaapClient("majoribanks.mcs.vuw.ac.nz", 3689);
			List<Track> music = client.getTrackList();
			InputStream in = client.getStream(music.get(0));
			int x = in.read();
//			while(x!=-1){
//				System.out.print(x);
//				x = in.read();
//			}
			
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
}
