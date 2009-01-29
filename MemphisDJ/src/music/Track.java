package music;


import interfaces.Album;
import interfaces.Constants;
import interfaces.HasMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class Track implements HasMetadata {
	
	private final int id;
	private final long persistentId;
	private Album album;
	private final Map<Constants, Object> tags = new HashMap<Constants, Object>();
	
	public Track(int id, long persistentId) {
		this.id = id;
		this.persistentId = persistentId; 
		this.tags.put(Constants.dmap_itemid, id);
		this.tags.put(Constants.dmap_persistentid, persistentId);
	}
	
	public int id() {
		return id;
	}
	
	public long persistentId() {
		return persistentId;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Track) {
			return ((Track)o).persistentId == this.persistentId;
		}
		return false;
	}
	
	public int hashCode() {
		return ((Long)persistentId).hashCode();
	}
	
	public Album getAlbum() {
		return album;
	}
	
	public void setAlbum(Album album) {
		this.album = album;
		tags.put(Constants.daap_songalbumid, album.id());
	}
	
	public Object get(Constants tag) {
		return tags.get(tag);
	}
	
	public void put(Constants tag, Object value) {
		tags.put(tag, value);
	}

	public Iterable<Constants> getAllTags() {
		return tags.keySet();
	}
	
	/**
	 * Returns the track as a playable stream of data.
	 * 
	 * @return A stream, possibly null
	 * @throws IOException
	 */
	public abstract InputStream getStream() throws IOException;
	
	public interface TrackFactory {
		public Track create(int id, long persistentId);
	}
}