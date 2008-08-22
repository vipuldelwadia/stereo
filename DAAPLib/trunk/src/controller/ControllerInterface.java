package controller;

import java.util.List;

import playlist.Track;



public interface ControllerInterface {

	/**
	 * pauses the playing track
	 * 
	 */
	public abstract void pauseTrack();

	/**
	 * plays the paused track
	 */
	public abstract void playTrack();
	
	public abstract void setPlaylist(List<Track> p);

	public abstract List<Track> getPlaylist();

	/**
	 * change the volume to the stated value
	 * 
	 * @param newVolume
	 *            int between 0 and 255
	 * @throws IllegalArgumentException
	 *             if newVolume is < 0 or > 255
	 */
	public abstract void changeVolume(int newVolume);

	/**
	 * skips to the next track
	 * 
	 */
	
	public abstract void skipTrack();

	public abstract int getVolume();

	public abstract void stop();
	
	public abstract void status();
	
	public abstract void filter(String type, String criteria);

	public abstract void recentlyPlayed();

	public abstract void displayQuery(String type, String crit);
}