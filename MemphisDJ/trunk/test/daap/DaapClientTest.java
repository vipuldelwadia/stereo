package daap;

import org.junit.Test;


public class DaapClientTest {
	
	@Test
	public void simpleTest(){
		new DaapClient("majoribanks.mcs.vuw.ac.nz", 3689);
	}
}
