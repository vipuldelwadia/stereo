package player;

import interfaces.CLI;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import playlist.Track;
import controller.ControllerInterface;


/**
 * 
 * @author coxdyla This class interfaces the User interfaces with the servers
 *         playlist TODO make it recieve and interpret DACP requests
 */
public class Controller implements ControllerInterface{
    
    private final static boolean    DEBUG    = false;
    
    private DACPHeckler              dacp;
    
    public Controller() {
        this.connect();
    }
    
    private boolean connect() {
        // TODO
        // Assumes once connected, it is always connected.
        
        if (this.dacp == null) {
            try {
                this.dacp = new DACPHeckler("fiebigs", 3689);
                return true;
            }
            catch (UnknownHostException e) {
                if (DEBUG)
                    e.printStackTrace();
            }
            catch (IOException e) {
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
    
    /* (non-Javadoc)
	 * @see player.ControllerInterface#pauseTrack()
	 */
    public void pauseTrack() {
        if (this.connect())
            this.dacp.pause();
    }
    
    /* (non-Javadoc)
	 * @see player.ControllerInterface#playTrack()
	 */
    public void playTrack() {
        if (this.connect())
            this.dacp.play();
    }
    
    /* (non-Javadoc)
	 * @see player.ControllerInterface#getPlaylist()
	 */
    public List<Track> getPlaylist(){
         return this.dacp.getTracks();
    }
    
    /* (non-Javadoc)
	 * @see player.ControllerInterface#changeVolume(int)
	 */
    public void changeVolume(int newVolume) {
        if (newVolume < 0 || newVolume > 255)
            throw new IllegalArgumentException("volume must be between 0-255");
        if (this.connect())
            this.dacp.setVolume(newVolume);
    }
    
    /* (non-Javadoc)
	 * @see player.ControllerInterface#skipTrack()
	 */
    public void skipTrack() {
        this.dacp.skip();
    }
    
    /* (non-Javadoc)
	 * @see player.ControllerInterface#getVolume()
	 */
    public int getVolume() {
    	return this.dacp.getVolume();
    }

	public void setPlaylist(List<Track> p) {
		ArrayList<Track> tracks = new ArrayList<Track>();
		for(Track t : tracks) tracks.add(t);
		this.dacp.setTracks(tracks);
	}

	public void stop() {
		this.dacp.stop();
	}

	public void filter(String type, String criteria) {
		// TODO Auto-generated method stub
		
	}

	public void status() {
		// TODO Auto-generated method stub
		
	}

	public void displayQuery(String type, String crit) {
		// TODO Auto-generated method stub
		
	}

	public void recentlyPlayed() {
		// TODO Auto-generated method stub
		
	}
	
	
	
    
    public static void main(String[] args) {
    	if (args.length == 0) {
    		new CLI(new Controller());
    	}
    	else {
    		String combinedArgs = "";
    		for(String s : args) {
    			combinedArgs += " " + s;
    		}
    		combinedArgs = combinedArgs.trim();
    		System.out.println(combinedArgs);
    		new CLI(new Controller(), combinedArgs);
    	}
    }
    
}
