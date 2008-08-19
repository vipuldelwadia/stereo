package test.interfaces;

import org.junit.Before;
import org.junit.Test;

import src.interfaces.GUI;
import src.playlist.Playlist;
import src.playlist.Song;


public class GUITest {

	GUI g;

	@Before
	public void testInstantiation(){
		g = new GUI();
		g.open();
	}

	@Test(expected = IllegalArgumentException.class)
	public void volumeSetNegative(){
		g.volumeUpdated(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void volumeSetGreaterThanTen(){
		g.volumeUpdated(11);
	}

	@Test
	public void volumeAcceptableValue(){
		g.volumeUpdated(0);
		g.volumeUpdated(4);
		g.volumeUpdated(10);
	}

	@Test
	public void trackPlayed() {
		g.trackPlayed();
	}

	@Test
	public void trackPaused() {
		g.trackPaused();
	}
	
	@Test
	public void playListUpdated() {
		g.playlistUpdated(new Playlist(null));
		// TODO make this more interesting as the playlist is more interesting
	}
	
	@Test (expected = NullPointerException.class)
	public void playListNull() {
		g.playlistUpdated(null);
	}
	
	@Test
	public void playingTrackUpdated() {
		g.playingTrackUpdated(new Song());
	}
	
	@Test (expected = NullPointerException.class)
	public void playingTrackNull() {
		g.playingTrackUpdated(null);
	}

}
