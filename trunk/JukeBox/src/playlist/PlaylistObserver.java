package playlist;

public interface PlaylistObserver {
	
	public void playListUpdated(Playlist p);

	public void trackEnded();

	public void trackPaused();

	public void trackPlayed();

	public void trackStarted(Track t);

	public void volumeChanged(int newVolume);

}
