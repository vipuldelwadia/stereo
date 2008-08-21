package client;

import static org.junit.Assert.*;

import org.junit.Test;

import writer.DACPRequestGenerator;


public class ClientBroadcasterTest {

	@Test
	public void testPlay() {
		assertTrue(DACPRequestGenerator.play().equals("playpause"));
	}

	@Test
	public void testPause() {
		assertTrue(DACPRequestGenerator.pause().equals("pause"));
	}

	@Test
	public void testChangeVolume() {
		double f=10;
		assertTrue(DACPRequestGenerator.changeVolume(f).equals("setproperty?dmcp.volume=10.0"));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidVolume() {
		double f=-1;
		DACPRequestGenerator.changeVolume(f);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidVolume2() {
		double f=256;
		DACPRequestGenerator.changeVolume(f);
	}
}
