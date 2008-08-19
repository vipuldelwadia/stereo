package test.controller;

import org.junit.Before;
import org.junit.Test;

import player.Controller;


public class ControllerTest {
	Controller c;

	@Before
	public void testInstantiation(){
		c = Controller.getInstance();
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
