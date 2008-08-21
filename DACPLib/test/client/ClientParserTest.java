package client;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import reader.DACPClientParser;



public class ClientParserTest {

	DACPClientParser client;

	@Before
	public void setup() {
		client = new DACPClientParser();
	}

	@Test
	public void parseTestArtist() {
		byte[] message = {'c', 'm', 's', 't', 0, 0, 0, 18, // cmst + size
				'c', 'a', 'n', 'a', 0, 0, 0, 10, // cana + size
				'T', 'o', 'm', '_', 'L', 'e', 'h', 'r', 'e','r'}; // Tom_Lehrer
		InputStream s = new ByteArrayInputStream(message);
		client.parse(s);
	}

	@Test
	public void parseTestCash() {
		byte[] message = {'c', 'm', 's', 't', 0, 0, 0, 9, // cmst + size
				'c', 'a', 's', 'h', 0, 0, 0, 1, // cash + size
				1}; // 1
		InputStream s = new ByteArrayInputStream(message);
		client.parse(s);
	}

	@Test
	public void parseTestArtistCash() {
		byte[] message = {'c', 'm', 's', 't', 0, 0, 0, 21, // cmst + size
				'c', 'a', 's', 'h', 0, 0, 0, 1, // cash + size
				1,
				'c', 'a', 'n', 'a', 0, 0, 0, 9, // cana + size
				'T', 'o', 'm', '_', 'L', 'e', 'h', 'r', 'e','r'}; // Tom_Lehrer}; 
		InputStream s = new ByteArrayInputStream(message);
		client.parse(s);
	}

	@Test
	public void parseLong() {
		byte[] message = {'c', 'm', 's', 't', 0, 0, 0, 21, // cmst + size
				'c', 'a', 'n', 'p', 0, 0, 0, 16, // canp + size
				0, 0, 0, 0, 0, 0, 0, 14,
				0, 0, 0, 0, 0, 0, 0, 1}; // 01 
		InputStream s = new ByteArrayInputStream(message);
		client.parse(s);
	}
	
	@Test
	public void parseSmallPlaylist() {
		byte[] message = {'a', 'p', 's', 'o', 0, 0, 0, 44,
				'm', 'l', 'c', 'l', 0, 0, 0, 36,
				'm', 'l', 'i', 't', 0, 0, 0, 28,
				'a', 's', 'a', 'r', 0, 0, 0, 6,
				'M', 'i', 'n', 'u', 'i', 't',
				'm', 'i', 'n', 'm', 0, 0, 0, 6,
				'S', 'o', 'v', 'i', 'e', 't'
				};
		InputStream s = new ByteArrayInputStream(message);
		client.parse(s);
	}
	
	@Test
	public void parsePlaylist() {
		byte[] message = {'a', 'p', 's', 'o', 0, 0, 0, 91,
				'm', 'l', 'c', 'l', 0, 0, 0, 83,
				'm', 'l', 'i', 't', 0, 0, 0, 39,
				'a', 's', 'a', 'r', 0, 0, 0, 6,
				'M', 'i', 'n', 'u', 'i', 't',
				'm', 'i', 'n', 'm', 0, 0, 0, 17,
				'S', 'o', 'v', 'i', 'e', 't', '_', 'A', 'i', 'r', 'h', 'o', 's', 't', 'e', 's', 's',
				'm', 'l', 'i', 't', 0, 0, 0, 28,
				'a', 's', 'a', 'r', 0, 0, 0, 6,
				'M', 'i', 'n', 'u', 'i', 't',
				'm', 'i', 'n', 'm', 0, 0, 0, 6,
				'C', 'l', 'a', 'i', 'r', 'e'};
		InputStream s = new ByteArrayInputStream(message);
		client.parse(s);
	}
}	
