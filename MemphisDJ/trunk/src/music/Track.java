package music;

import java.util.Date;
import java.util.Map;

import daap.DaapEntry;
import daap.DaapUtilities;

public class Track {
	private final int trackId;
	private final String album;
	private final String name;
	private final String artist;
//	private final short beatsPerMinute;
//	private final short bitrate;
//	private final String comment;
//	private final byte compilation;
//	private final Date dateAdded;
//	private final Date dateModified;
//	private final short discCount;
//	private final short discNumber;
//	private final byte disabled;
//	private final String eqPreset;
//	private final String format;
//	private final String genre;
//	private final String description;
//	private final byte relativeVolume;
//	private final int sampleRate;
//	private final int size;
	private final int startTime;
	private final int stopTime;
	private final int time;
//	private final short trackCount;
//	private final short tracknumber;
//	private final byte userRating;
//	private final short year;
//	private final byte dataKind;
//	private final String dataUrl;
	
	public Track(Map<Integer, Object> values){
		trackId = (Integer)values.get(DaapUtilities.stringToInt("miid"));
		name = (String)values.get(DaapUtilities.stringToInt("minm"));
		artist = (String) values.get(DaapUtilities.stringToInt("asar"));
		startTime=(Integer) values.get(DaapUtilities.stringToInt("asst"));
		stopTime=(Integer) values.get(DaapUtilities.stringToInt("assp"));
		time =(Integer) values.get(DaapUtilities.stringToInt("astm"));
		album = (String)values.get(DaapUtilities.stringToInt("asal"));
	}

	public String getAlbum() {
		return album;
	}

	public String getArtist() {
		return artist;
	}

	public String getName() {
		return name;
	}

	public int getStartTime() {
		return startTime;
	}

	public int getStopTime() {
		return stopTime;
	}

	public int getTime() {
		return time;
	}

	public int getTrackId() {
		return trackId;
	}
	
	
}
