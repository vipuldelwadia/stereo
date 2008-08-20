package playlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Playlist implements Iterable<Song>{
    private final static List<Song> EMPTY_SONGLIST = new ArrayList<Song>(); 
    public final static Playlist EMPTY_PLAYLIST = new Playlist(EMPTY_SONGLIST);
    
    private final List<Song> playlist;
    
    public Playlist(List<Song> songs) {
        if (songs == null)
            throw new NullPointerException("songs cannot be null");
        this.playlist = Collections.unmodifiableList(songs);
    }
    
    public Playlist(String xml) {
        if (xml == null)
            throw new NullPointerException("xml cannot be null");
        //TODO
        // XML Parsing -> playlist 
        // Here:
        
        //TEMP
        this.playlist = EMPTY_SONGLIST;
        
        //
    }
    
    public void sortField(String field) {
        // TODO
    }
    
    public Iterable<Song> getSongs() {
        return this.playlist;
    }
    
    public int size() {
        return this.playlist.size();
    }
    
    public Song getSong(int index) {
        if (index >= 0 && index < this.playlist.size())
            return this.playlist.get(index);
        return null;
    }

    public Iterator<Song> iterator() {
        return this.playlist.iterator();
    }
}
