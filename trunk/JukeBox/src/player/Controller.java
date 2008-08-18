package player;


/**
 * 
 * @author coxdyla
 *This class interfaces the User interfaces with the servers playlist
 */
public class Controller {
	
	private static Controller instance = new Controller();
	
	private Controller() {}
	
	public static Controller getInstance() {
		return instance;
	}
	
	/**
	 * pauses the playing track
	 *
	 */
	public void pauseTrack() {
		// TODO Auto-generated method stub (ha!)
		System.out.println("pause track");
	}

	/**
	 * plays the paused track
	 */
	public void playTrack() {
		// TODO Auto-generated method stub
		System.out.println("play track");
	}

	/**
	 * change the volume to the stated value
	 * @param newVolume int between 0 and 10
	 * @throws IllegalArgumentException if newVolume is < 0 or > 10
	 */
	public void changeVolume(int newVolume) {
		if (newVolume < 0 || newVolume > 10) throw new IllegalArgumentException("volume must be between 0-10");
		// TODO Auto-generated method stub
		System.out.println("change volume: " + newVolume);
	}

}
