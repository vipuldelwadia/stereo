package daap;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import music.Track;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class DaapClientTest {
	private static DaapClient client;
	
	@BeforeClass
	public static void setup(){
		try {
			client = new DaapClient("serranos.mcs.vuw.ac.nz", 3689);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

//	@Test
//	public void playTest(){
//
//			try {
//				List<Track> music = client.getTrackList();
//				InputStream in = client.getStream(music.get(0));
//				int x = in.read();
////			while(x!=-1){
////				System.out.print(x);
////				x = in.read();
////			}
//				
//				in.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	
//	}
	
	@Test
	public void isAlivetest(){


		assertTrue(client.isAlive());

	}
	
	@Test
	public void isDead(){


	
			System.out.println("sleeping ...");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			assertFalse(client.isAlive());
			
	}
	
	@AfterClass
	public static void teardown(){
		client = null;
	}
}
