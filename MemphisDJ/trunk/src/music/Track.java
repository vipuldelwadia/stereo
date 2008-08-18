package music;

import java.util.Date;

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
	
	public Track(DaapEntry entry){
		for(DaapEntry e:entry){
			
		}
		
	}
}
