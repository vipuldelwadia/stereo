package server;

import static org.junit.Assert.*;

import org.junit.Test;

import daccpserver.command.*;
import dacpserver.ServerParser;


public class ServerParserTest {

	@Test
	public void testParsePlay() {
		assertTrue(ServerParser.parse("GET ctrl-int/1/playpause HTTP/1.1") instanceof Play);
	}
	
	@Test
	public void testParsePause() {
		assertTrue(ServerParser.parse("GET ctrl-int/1/pause HTTP/1.1") instanceof Pause);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidCommand() {
		ServerParser.parse("GET ctrl-int/1/puase HTTP/1.1");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidControl() {
		ServerParser.parse("GET ctrl-itn/1/pause HTTP/1.1");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidRequestFormat() {
		ServerParser.parse("GET ctrl-int/1/pause");
	}
	
	@Test 
	public void testVolume(){
		assertTrue(ServerParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=0&felix=cool HTTP/1.1") instanceof SetVolume);
		assertTrue(ServerParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=100&felix=cool HTTP/1.1") instanceof SetVolume);
		assertTrue(ServerParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=255&felix=cool HTTP/1.1") instanceof SetVolume);
		assertTrue(ServerParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=0.1&felix=cool HTTP/1.1") instanceof SetVolume);
		assertTrue(ServerParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=254.9&felix=cool HTTP/1.1") instanceof SetVolume);
		assertTrue(ServerParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=100.100&felix=cool HTTP/1.1") instanceof SetVolume);
		
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidNumberVolume1() {
		ServerParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=-0.1&felix=cool HTTP/1.1");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidNumberVolume2() {
		ServerParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=-100.50&felix=cool HTTP/1.1");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidNumberVolume3() {
		ServerParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=255.001&felix=cool HTTP/1.1");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidNumberVolume4() {
		ServerParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=10000.00&felix=cool HTTP/1.1");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidVolume() {
		ServerParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=abc&felix=cool HTTP/1.1");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidParameters() {
		ServerParser.parse("GET ctrl-int/1/setproperty?dmcpvolume=abc&felix=cool HTTP/1.1");
	}

}
