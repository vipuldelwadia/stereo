package server;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import reader.DACPServerParser;
import util.command.DACPPause;
import util.command.DACPPlay;
import util.command.DACPSetVolume;



public class ServerParserTest {

	@Test
	public void testParsePlay() {
		assertTrue(DACPServerParser.parse("GET ctrl-int/1/playpause HTTP/1.1") instanceof DACPPlay);
	}
	
	@Test
	public void testParsePause() {
		assertTrue(DACPServerParser.parse("GET ctrl-int/1/pause HTTP/1.1") instanceof DACPPause);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidCommand() {
		DACPServerParser.parse("GET ctrl-int/1/puase HTTP/1.1");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidControl() {
		DACPServerParser.parse("GET ctrl-itn/1/pause HTTP/1.1");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidRequestFormat() {
		DACPServerParser.parse("GET ctrl-int/1/pause");
	}
	
	@Test 
	public void testVolume(){
		assertTrue(DACPServerParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=0&felix=cool HTTP/1.1") instanceof DACPSetVolume);
		assertTrue(DACPServerParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=100&felix=cool HTTP/1.1") instanceof DACPSetVolume);
		assertTrue(DACPServerParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=255&felix=cool HTTP/1.1") instanceof DACPSetVolume);
		assertTrue(DACPServerParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=0.1&felix=cool HTTP/1.1") instanceof DACPSetVolume);
		assertTrue(DACPServerParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=254.9&felix=cool HTTP/1.1") instanceof DACPSetVolume);
		assertTrue(DACPServerParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=100.100&felix=cool HTTP/1.1") instanceof DACPSetVolume);
		
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidNumberVolume1() {
		DACPServerParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=-0.1&felix=cool HTTP/1.1");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidNumberVolume2() {
		DACPServerParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=-100.50&felix=cool HTTP/1.1");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidNumberVolume3() {
		DACPServerParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=255.001&felix=cool HTTP/1.1");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidNumberVolume4() {
		DACPServerParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=10000.00&felix=cool HTTP/1.1");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidVolume() {
		DACPServerParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=abc&felix=cool HTTP/1.1");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidParameters() {
		DACPServerParser.parse("GET ctrl-int/1/setproperty?dmcpvolume=abc&felix=cool HTTP/1.1");
	}

}
