package daap;

import interfaces.Album;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import music.Track;
import util.DACPConstants;

public class DAAPTrack extends Track {
	
	private DAAPClient publisher;
	
	public static Map<Integer, Map<Integer, DAAPTrack>> dbToIntToTrack = new HashMap<Integer, Map<Integer,DAAPTrack>>();
	
	public static DAAPTrack create(DAAPEntry entry, DAAPClient parent) {
		
		Map<Integer, DAAPTrack> map = dbToIntToTrack.get(parent.id());
		if (map == null) {
			map = new HashMap<Integer, DAAPTrack>();
			dbToIntToTrack.put(parent.id(), map);
		}
		
		int id = 0;
		long per = 0;
		String name = null;
		String artist = null;
		String album = null;
		
		for (DAAPEntry e: entry.children()) {
			switch (e.code()) {
			case DAAPConstants.miid:
				id = (Integer)e.value(); break;
			case DAAPConstants.mper:
				per = (Long)e.value(); break;
			case DAAPConstants.minm:
				name = (String)e.value(); break;
			case DAAPConstants.ARTIST:
				artist = (String)e.value(); break;
			case DAAPConstants.ALBUM:
				album = (String)e.value(); break;
			}
		}
		
		if (per == 0) {
			if (name == null) name = "";
			if (artist == null) artist = "";
			
			int na = name.hashCode();
			int ar = artist.hashCode();
			
			byte[] persistant = new byte[8];
			persistant[0] = (byte)(na>>24 & 0xFF);
			persistant[1] = (byte)(na>>16 & 0xFF);
			persistant[2] = (byte)(na>>8  & 0xFF);
			persistant[3] = (byte)(na	  & 0xFF);
			persistant[4] = (byte)(ar>>24 & 0xFF);
			persistant[5] = (byte)(ar>>16 & 0xFF);
			persistant[6] = (byte)(ar>>8  & 0xFF);
			persistant[7] = (byte)(ar	  & 0xFF);
			
			per = new BigInteger(persistant).longValue();
		}

		if (id == 0) return null;
		
		DAAPTrack track;
		if (map.containsKey(id)) {
			track = map.get(id);
		}
		else {
			track = new DAAPTrack(id, per, parent);
			map.put(id, track);
		}
		
		for (DAAPEntry e: entry.children()) {
			track.put(e.code(), e.value());
		}
		
		DAAPAlbum da = DAAPAlbum.createAlbum(album, artist);
		track.setAlbum(da);
		
		return track;
	}
	
	private DAAPTrack(int id, long persistent, DAAPClient parent) {
		super(id, persistent);
		publisher = parent;
	}
	
	public DAAPClient getParent(){
		return publisher;
	}
	
	public String toString() {
		 return String.format("%s",get(DAAPConstants.NAME));
	}

	public void setAlbum(Album album) {
		super.setAlbum(album);
		put(DACPConstants.asai, album.get(DACPConstants.asai));
	}
	
	public InputStream getStream() throws IOException{
		return publisher.getStream(this);
	}
	
}