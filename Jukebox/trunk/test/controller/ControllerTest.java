package controller;

import org.junit.Before;
import org.junit.Test;

import player.Controller;
import player.ControllerInterface;


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
	public void volumeSetGreaterThanTen(){
		c.changeVolume(11);
	}
	
	@Test
	public void volumeAcceptableValue(){
		c.changeVolume(0);
		c.changeVolume(4);
		c.changeVolume(10);
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
