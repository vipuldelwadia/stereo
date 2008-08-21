package player;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import controller.ControllerInterface;

import playlist.Playlist;
import playlist.Track;


import daccpclient.DACPClient;


/**
 * 
 * @author coxdyla This class interfaces the User interfaces with the servers
 *         playlist TODO make it recieve and interpret DACP requests
 */
public class Controller implements ControllerInterface{
    
    private final static boolean    DEBUG    = false;
    
    private DACPClient              dacp;
    
    public Controller() {
        this.connect();
    }
    
    private boolean connect() {
        // TODO
        // Assumes once connected, it is always connected.
        
        if (this.dacp == null) {
            try {
                this.dacp = new DACPClient("climie", 3689);
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
    public Playlist getPlaylist(){
        //NASTY temp hack
        List<Track> tracks = new ArrayList<Track>();
//        tracks.add(new Song("Lithium0", "Nirvana", "", "Rock", 260));
//        tracks.add(new Song("Lithium1", "Nirvana", "", "Rock", 260));
//        tracks.add(new Song("Lithium2", "Nirvana", "", "Rock", 260));
//        tracks.add(new Song("Lithium3", "Nirvana", "", "Rock", 260));
//        tracks.add(new Song("Lithium4", "Nirvana", "Hate the World", "Rock", 260));
//        tracks.add(new Song("Lithium5", "Nirvana", "", "Rock", 260));
//        tracks.add(new Song("Lithium6", "Nirvana", "", "Rock", 260));
//        tracks.add(new Song("Lithium7", "Nirvana", "", "Rock", 260));
        tracks = (Math.random()>0.8)?new ArrayList<Track>():tracks.subList(0,(int)(Math.random()*tracks.size()));
        return new Playlist(tracks);
        //String xml = this.dacp.getXML("PLAYLIST");
        //return new Playlist(xml); 
    }
    
    /* (non-Javadoc)
	 * @see player.ControllerInterface#changeVolume(int)
	 */
    public void changeVolume(int newVolume) {
        if (newVolume < 0 || newVolume > 100)
            throw new IllegalArgumentException("volume must be between 0-100");
        if (this.connect())
            this.dacp.setVolume(newVolume*2.55);
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
        String xml = this.dacp.getXML("VOLUME");
        
        //PARSE xml -> int here:
        
        //
        
        int volume = 0;
        
        return volume;
    }

	public void setPlaylist(Playlist p) {
		// TODO Auto-generated method stub
		
	}
}
