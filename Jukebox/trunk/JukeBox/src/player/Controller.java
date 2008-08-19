package player;

import java.io.IOException;
import java.net.UnknownHostException;


/**
 * 
 * @author coxdyla
 *This class interfaces the User interfaces with the servers playlist
 * TODO make it recieve and interpret DACP requests
 */
public class Controller {
	
	private final static Controller instance = new Controller();
	private DACPClient dacp;
	private static String host = "localhost";
	private static int port = 51234;
	
	private Controller() {
		try {
			dacp = new DACPClient("cafe-baba", 51234);
		} catch (UnknownHostException e) {
			unknownHost(e);
		} catch (IOException e) {
			ioException(e);
		}
	}
	
	private void unknownHost(UnknownHostException e) {
		e.printStackTrace();
		System.exit(1);
	}
	
	private void ioException(IOException e) {
		e.printStackTrace();
		System.exit(1);
	}
	
	public static Controller getInstance() {
		return instance;
	}
	
	/**
	 * pauses the playing track
	 *
	 */
	public void pauseTrack() {
		try {
			dacp.pause();
		} catch (IOException e) {
			ioException(e);
		}
	}

	/**
	 * plays the paused track
	 */
	public void playTrack() {
		try {
			dacp.play();
		} catch (IOException e) {
			ioException(e);
		}
	}

	/**
	 * change the volume to the stated value
	 * @param newVolume int between 0 and 10
	 * @throws IllegalArgumentException if newVolume is < 0 or > 10
	 */
	public void changeVolume(int newVolume) {
		if (newVolume < 0 || newVolume > 10) throw new IllegalArgumentException("volume must be between 0-10");
		dacp.changeVolume(newVolume);
	}
	
	/**
	 * skips to the next track
	 * 
	 */
	public void skipTrack() {
		// TODO Auto-generated method stub (ha!)
		System.out.println("skip track");
	}

	public void getVolume() {
		// TODO Auto-generated method stub
		
		
	}
}
