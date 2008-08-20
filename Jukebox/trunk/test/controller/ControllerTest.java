package test.controller;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import src.player.Controller;


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
		try {
            c.playTrack();
        }
        catch (IOException e) {
            fail("Input/Output exception");
        }
	}
	
	@Test
	public void trackPaused() {
		try {
            c.pauseTrack();
        }
        catch (IOException e) {
            fail("Input/Output exception");
        }
	}
	
}
