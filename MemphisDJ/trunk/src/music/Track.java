package music;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import daap.DaapClient;
import daap.DaapUtilities;

public class Track {
	private static final int TRACKID = DaapUtilities.stringToInt("miid");
	public static final int ALBUM = DaapUtilities.stringToInt("asal");
	public static final int ARTIST = DaapUtilities.stringToInt("asar");
	public static final int BITRATE = DaapUtilities.stringToInt("asbr");
	public static final int COMPOSER = DaapUtilities.stringToInt("ascp");
	public static final int GENRE = DaapUtilities.stringToInt("asgn");
	public static final int NAME = DaapUtilities.stringToInt("minm");
	public static final int TIME = DaapUtilities.stringToInt("astm");
	public static final int START_TIME = DaapUtilities.stringToInt("asst");
	public static final int STOP_TIME = DaapUtilities.stringToInt("assp");
	
	private Map<Integer, Object> tags;
	private DaapClient publisher;
	
	public Track(Map<Integer, Object> values,DaapClient parent){
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
	
	public boolean isParent(DaapClient dad){
		return publisher == dad;
	}
	
	/**Returns null if the connection closed*/
	public InputStream getStream() throws IOException{
		return publisher.getStream(this);
	}
	
}
