package player;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import clinterface.CLI;

import playlist.Track;
import controller.ControllerInterface;
import daap.DAAPConstants;

/**
 * 
 * @author coxdyla This class interfaces the User interfaces with the servers
 *         playlist TODO make it recieve and interpret DACP requests
 */
public class Controller implements ControllerInterface {

	private final static boolean DEBUG = false;

	private DACPHeckler dacp;

	public Controller() {
		this.connect();
	}

	private boolean connect() {
		// TODO
		// Assumes once connected, it is always connected.

		if (this.dacp == null) {
			try {
				this.dacp = new DACPHeckler("cafe-bodega", 3689);
				return true;
			} catch (UnknownHostException e) {
				if (DEBUG)
					e.printStackTrace();
			} catch (IOException e) {
				if (DEBUG)
					e.printStackTrace();
			}
			return false;
		}
		return true;
	}

	public boolean isValidController() {
		return this.dacp != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see player.ControllerInterface#pauseTrack()
	 */
	public void pauseTrack() {
		if (this.connect())
			this.dacp.pause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see player.ControllerInterface#playTrack()
	 */
	public void playTrack() {
		if (this.connect())
			this.dacp.play();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see player.ControllerInterface#getPlaylist()
	 */
	public List<Track> getPlaylist() {
		if (this.connect())
			return this.dacp.getTracks();
		return new ArrayList<Track>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see player.ControllerInterface#changeVolume(int)
	 */
	public void changeVolume(int newVolume) {
		if (newVolume < 0 || newVolume > 255)
			throw new IllegalArgumentException("volume must be between 0-255");
		if (this.connect())
			this.dacp.setVolume(newVolume);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see player.ControllerInterface#skipTrack()
	 */
	public void skipTrack() {
		if (this.connect())
			this.dacp.skip();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see player.ControllerInterface#getVolume()
	 */
	public int getVolume() {
		if (this.connect())
			return this.dacp.getVolume();
		return 0;
	}

	/*
	 * 
	 */
	public void setPlaylist(List<Track> p) {
		ArrayList<Track> tracks = new ArrayList<Track>();
		for (Track t : tracks)
			tracks.add(t);
		if (this.connect())
			this.dacp.setTracks(tracks);
	}

	public void stop() {
		if (this.connect())
			this.dacp.stop();
	}

	public void status() {
		if (this.connect()) {
			Track t = this.dacp.queryCurrentTrack();
			if (t != null) // TODO print this better? What about
				// paused/unpause?
				System.out.println(t.toString());
		}

	}

	public void createPlaylistWithFilter(String type, String criteria) {
		if (this.connect()) {
			Map<Integer, String> filter = new HashMap<Integer, String>();
			filter = fillFilter(type, criteria, filter);
			this.dacp.setPlaylistWithFilter(filter);
		}
	}

	public void queryLibrary(String type, String crit) {
		if (this.connect()) {
			Map<Integer, String> filter = new HashMap<Integer, String>();
			filter = fillFilter(type, crit, filter);
			for (Track currentTrack : this.dacp.getPlaylistWithFilter(filter))
				System.out.print(currentTrack);
		}
	}

	// helper method
	/**
	 * Returns a filtered playlist without replacing the current one.
	 * 
	 * @param type
	 * @param criteria
	 */
	private Map<Integer, String> fillFilter(String type, String criteria,
			Map<Integer, String> playList) {
		if (type.equalsIgnoreCase("artist"))
			playList.put(DAAPConstants.ARTIST, criteria);
		else if (type.equalsIgnoreCase("album"))
			playList.put(DAAPConstants.ALBUM, criteria);

		return playList;
	}

	public void queryRecentlyPlayed() {
		if (this.connect()) {
			List<Track> recent = this.dacp.getRecentlyPlayedTracks();

			System.out.println("Recently played Music\n-------------------");
			while (!recent.isEmpty())
				System.out.print(recent.remove(0));
		}

	}

	public void append(String type, String crit) {
		// TODO
	}

	public void displayLibrary() {
		if (this.connect()) {
			System.out.println("The Library Contents\n-------------------");
			for (Track currentTrack : this.dacp.getLibrary())
				System.out.print(currentTrack.toString());
		}

	}

	public static void main(String[] args) {
		if (args.length == 0) {
			new CLI(new Controller());
		} else {
			String combinedArgs = "";
			for (String s : args) {
				combinedArgs += " " + s;
			}
			combinedArgs = combinedArgs.trim();
			System.out.println(combinedArgs);
			new CLI(new Controller(), combinedArgs);
		}
	}

}
