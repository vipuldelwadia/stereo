package client;

import static org.junit.Assert.*;

import org.junit.Test;

import dacpclient.DACPClientBroadcaster;

public class ClientBroadcasterTest {

	@Test
	public void testPlay() {
		assertTrue(DACPClientBroadcaster.play().equals("playpause"));
	}

	@Test
	public void testPause() {
		assertTrue(DACPClientBroadcaster.pause().equals("pause"));
	}

	@Test
	public void testChangeVolume() {
		double f=10;
		assertTrue(DACPClientBroadcaster.changeVolume(f).equals("setproperty?dmcp.volume=10.0"));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidVolume() {
		double f=-1;
		DACPClientBroadcaster.changeVolume(f);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidVolume2() {
		double f=256;
		DACPClientBroadcaster.changeVolume(f);
	}
}
