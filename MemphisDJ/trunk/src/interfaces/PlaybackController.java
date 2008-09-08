package interfaces;

import java.util.List;


public interface PlaybackController {

	public void pause();
	public void play();
	public void next();
	public void stop();
	
	public void setPlaylist(List<Track> p);
	public List<Track> getPlaylist();

	public void changeVolume(int newVolume);
	public int getVolume();

	public String status();

	public void createPlaylistWithFilter(String type, String criteria);
	public List<Track> queryRecentlyPlayed();
	public List<Track> queryLibrary(String type, String crit);
	public List<Track> getLibrary();

	public void append(String type, String crit);

}