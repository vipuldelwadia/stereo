package src.player;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import src.playlist.Playlist;
import src.playlist.Song;
import daccpclient.DACPClient;

/**
 * 
 * @author coxdyla This class interfaces the User interfaces with the servers
 *         playlist TODO make it recieve and interpret DACP requests
 */
public class Controller {
    
    private final static boolean    DEBUG    = false;
    private final static Controller instance = new Controller();
    
    private DACPClient              dacp;
    
    private Controller() {
        this.connect();
    }
    
    private boolean connect() {
        // TODO
        // Assumes once connected, it is always connected.
        
        if (this.dacp == null)
            try {
                this.dacp = new DACPClient("playground", 3689);
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
    
    public static Controller getInstance() {
        return instance;
    }
    
    public boolean isValidController() {
        return this.dacp != null;
    }
    
    /**
     * pauses the playing track
     * 
     */
    public void pauseTrack() {
        if (this.connect())
            this.dacp.pause();
    }
    
    /**
     * plays the paused track
     */
    public void playTrack() {
        if (this.connect())
            this.dacp.play();
    }
    
    public Playlist getPlaylist(){
        String xml = this.dacp.getXML("PLAYLIST");
        return new Playlist(xml); 
    }
    
    /**
     * change the volume to the stated value
     * 
     * @param newVolume
     *            int between 0 and 10
     * @throws IllegalArgumentException
     *             if newVolume is < 0 or > 10
     */
    public void changeVolume(int newVolume) {
        if (newVolume < 0 || newVolume > 10)
            throw new IllegalArgumentException("volume must be between 0-10");
        if (this.connect())
            this.dacp.setVolume(newVolume*25.5);
    }
    
    /**
     * skips to the next track
     * 
     */
    public void skipTrack() {
        this.dacp.skip();
    }
    
    public int getVolume() {
        String xml = this.dacp.getXML("VOLUME");
        
        //PARSE xml -> int here:
        
        //
        
        int volume = 0;
        
        return volume;
    }
}
