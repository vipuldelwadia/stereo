package playlist;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import daap.DAAPClient;
import daap.DAAPConstants;

public class Track {
	
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
		if (tags.containsKey(tagID)) {
			return tags.get(tagID);			
		}
		else return null;
	}

	public int getTrackId() {
		return (Integer)tags.get(DAAPConstants.TRACK_ID);
	}
	
	public DAAPClient getParent(){
		return publisher;
	}
	
	/**Returns null if the connection closed*/
	public InputStream getStream() throws IOException{
		return publisher.getStream(this);
	}
	
	public String toString(){
		 return String.format("%s - %s - %s\n",getTag(DAAPConstants.NAME),getTag(DAAPConstants.ARTIST),getTag(DAAPConstants.ALBUM));
	}
	
}
