package src.playlist;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Playlist implements Iterable<Song>{
    private final List<Song> playlist;
    
    public Playlist(List<Song> songs) {
        if (songs == null)
            throw new NullPointerException("songs cannot be null");
        this.playlist = Collections.unmodifiableList(songs);
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
