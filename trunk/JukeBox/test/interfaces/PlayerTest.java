package interfaces;

import interfaces.Player;

import org.junit.*;

import playlist.Playlist;
import playlist.Track;


public class PlayerTest {
	Player player;

	@Before
	public void testInstantiation(){
		player = new Player();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void volumeSetNegative(){
		player.volumeChanged(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void volumeSetGreaterThanTen(){
		player.volumeChanged(11);
	}
	
	@Test
	public void volumeAcceptableValue(){
		player.volumeChanged(0);
		player.volumeChanged(4);
		player.volumeChanged(10);
	}
	
	@Test(expected = NullPointerException.class)
	public void playListNull(){
		player.playListUpdated(null);
	}
	
	@Test
	public void playListNotNull(){
		//TODO: once Playlist implemented, make this more interesting
		player.playListUpdated(new Playlist());
	}
	
	@Test(expected = IllegalStateException.class)
	public void trackEndedNoTrackPlaying(){
		player.trackEnded();
	}
	
	@Test
	public void trackEndedTrackPlaying(){
		//TODO: track ends with elapsed time > track length
		player.trackStarted(new Track());
		player.trackEnded();
	}
	
	@Test
	public void trackEndedTrackPaused() {
		player.trackStarted(new Track());
		player.trackPaused();
		player.trackEnded();
	}
	
	@Test
	public void trackEndedNewTrack() {
		player.trackStarted(new Track());
		player.trackEnded();
		player.trackStarted(new Track());
	}
	

	
	@Test(expected = IllegalStateException.class)
	public void trackStartedNoTrack(){
		player.trackStarted(null);
	}

	@Test(expected = IllegalStateException.class)
	public void trackPlayedNoTrack(){
		player.trackPlayed();
	}
}
