package daap;

import static org.junit.Assert.*;

import java.io.IOException;

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
}
