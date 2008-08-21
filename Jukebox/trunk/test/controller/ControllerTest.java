package controller;

import org.junit.Before;
import org.junit.Test;

import player.Controller;


public class ControllerTest {
	ControllerInterface c;

	@Before
	public void testInstantiation(){
		c = new Controller();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void volumeSetNegative(){
		c.changeVolume(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void volumeSetGreaterThan255(){
		c.changeVolume(256);
	}
	
	@Test
	public void volumeAcceptableValue(){
		c.changeVolume(0);
		c.changeVolume(40);
		c.changeVolume(255);
	}
	
	@Test
	public void trackPlayed() {
		c.playTrack();
	}
	
	@Test
	public void trackPaused() {
		c.pauseTrack();
	}
	
}
