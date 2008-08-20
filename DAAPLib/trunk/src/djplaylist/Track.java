package djplaylist;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import daap.DAAPClient;
import daap.DAAPUtilities;

public class Track {
	private static final int TRACKID = DAAPUtilities.stringToInt("miid");
	public static final int ALBUM = DAAPUtilities.stringToInt("asal");
	public static final int ARTIST = DAAPUtilities.stringToInt("asar");
	public static final int BITRATE = DAAPUtilities.stringToInt("asbr");
	public static final int COMPOSER = DAAPUtilities.stringToInt("ascp");
	public static final int GENRE = DAAPUtilities.stringToInt("asgn");
	public static final int NAME = DAAPUtilities.stringToInt("minm");
	public static final int TIME = DAAPUtilities.stringToInt("astm");
	public static final int START_TIME = DAAPUtilities.stringToInt("asst");
	public static final int STOP_TIME = DAAPUtilities.stringToInt("assp");
	
	private Map<Integer, Object> tags;
	private DAAPClient publisher;
	
	public Track(Map<Integer, Object> values,DAAPClient parent){
		tags = new HashMap<Integer, Object>();
		
		for (Integer key: values.keySet()) {
			tags.put(key, values.get(key));
		}
		
		publisher = parent;
	}

	/**
	 * tagId should take a value from thos available in Track
	 * @param tagID
	 * @return
	 */
	public Object getTag(int tagID){
		return tags.get(tagID);
	}

	public int getTrackId() {
		return (Integer)tags.get(TRACKID);
	}
	
	public DAAPClient getParent(){
		return publisher;
	}
	
	/**Returns null if the connection closed*/
	public InputStream getStream() throws IOException{
		return publisher.getStream(this);
	}
	
}
