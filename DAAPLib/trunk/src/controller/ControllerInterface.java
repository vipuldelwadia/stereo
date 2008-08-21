package controller;

import playlist.Playlist;



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
	
	public abstract void setPlaylist(Playlist p);

	public abstract Playlist getPlaylist();

	/**
	 * change the volume to the stated value
	 * 
	 * @param newVolume
	 *            int between 0 and 10
	 * @throws IllegalArgumentException
	 *             if newVolume is < 0 or > 10
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
}