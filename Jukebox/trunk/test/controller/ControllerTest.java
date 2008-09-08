package controller;

import interfaces.PlaybackController;

import org.junit.Before;
import org.junit.Test;

import player.Controller;


public class ControllerTest {
	PlaybackController c;

	@Before
	public void testInstantiation(){
		c = new Controller("fiebigs", 3689);
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
		c.play();
	}
	
	@Test
	public void trackPaused() {
		c.pause();
	}
	
}
