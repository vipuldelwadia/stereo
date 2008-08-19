package client;

import static org.junit.Assert.*;

import org.junit.Test;

public class ClientBroadcasterTest {

	@Test
	public void testPlay() {
		assertTrue(ClientBroadcaster.play().equals("playpause"));
	}

	@Test
	public void testPause() {
		assertTrue(ClientBroadcaster.pause().equals("pause"));
	}

	@Test
	public void testChangeVolume() {
		double f=10;
		assertTrue(ClientBroadcaster.changeVolume(f).equals("setproperty?dmcp.volume=10.0"));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidVolume() {
		double f=-1;
		ClientBroadcaster.changeVolume(f);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidVolume2() {
		double f=256;
		ClientBroadcaster.changeVolume(f);
	}
}
