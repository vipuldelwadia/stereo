package daap;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;



import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.HttpURLConnection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SimpleLog;

import djplaylist.Track;

public class DAAPClient {

	private String hostname;

	private int dbid;

	private int revisionNumber;

	private Log log;

	private int sessionID;

	private int port;

	DAAPUtilities helper;

	public DAAPClient(String hostname, int port) throws IOException {
		//This method should login and get a session ID,
		//the database ID (dbid) by updateing and currentVersion [of database]
		this.hostname = hostname;
		this.port = port;

		log = new SimpleLog("Log");

		try {
			helper = new DAAPUtilities(hostname, log);
		} catch (IOException e) {
			System.out.println("Probably an invalid host");
			e.printStackTrace();
		}
		//Get a session Id
		sessionID = getSessionID();
		revisionNumber = getRevisionNumber();
		dbid = getDatabaseId();

	}

	public List<Track> getTrackList() {	
		if(dbid < 0) return null;
		
		List<Track> tracks = new ArrayList<Track>();

		String request = "databases/"
			+ dbid
			+ "/items?type=music&meta=dmap.itemkind,dmap.itemid,dmap.itemname,daap.songalbum,daap.songartist,daap.songgenre,daap.songcomposer,daap.songbitrate,daap.songsamplerate,daap.songstarttime,daap.songstoptime,daap.songtime&session-id="
			+ sessionID + "&revision-id=" + revisionNumber;

		InputStream in = null;
		
		try {
			in = helper.request(hostname, request, log);
			DAAPEntry entry = DAAPEntry.parseStream(in, helper.types);

			if ((entry == null)
					|| (entry.getName() != DAAPUtilities.stringToInt("adbs"))) {
				return null;
			}

			for (DAAPEntry e : entry) {
				if (e.getName() == DAAPUtilities.stringToInt("mlcl")) {
					entry = e;
					break;
				}
			}

			for (DAAPEntry e : entry) {
				if ((entry == null) || !entry.hasChildren()) {
					continue;
				}
				final Map<Integer, Object> values = e.getValueMap();
				tracks.add(new Track(values, this));

			}

			return tracks;
		} catch (IOException e) {
			return null;
		}finally{
			helper.release(in);
		}
	}

	public InputStream getStream(Track track) throws IOException {
		int song = track.getTrackId();
		return helper.request(hostname, "databases/" + dbid + "/items/" + song
				+ ".mp3?session-id=" + sessionID, log);
	}

	public boolean isUpdated() throws ClientExpiredException{
		int newRevisionNumber = getRevisionNumber();
		if(newRevisionNumber == -1) throw new ClientExpiredException();
		if(revisionNumber != newRevisionNumber){
			revisionNumber = newRevisionNumber;
			return true;
		}else{
			return false;
		}
	}

	private int getSessionID() {
		String loginRequest = "login";
		InputStream in = null;

		try {
			in = helper.request(hostname, loginRequest, log);
			DAAPEntry entry = DAAPEntry.parseStream(in, helper.types);

			for (DAAPEntry e : entry) {
				if (e.getName() == DAAPUtilities.stringToInt("mlid")) {
					return (Integer) e.getValue();
				}
			}

			return -1;
		} catch (IOException e) {
			return -1;
		}finally{
			helper.release(in);
		}

	}

	private int getRevisionNumber() {
		if(sessionID < 0) return -1;
		
		InputStream in = null;

		String request = "update?session-id=" + sessionID;

		try {
			in = helper.request(hostname, request, log);
			DAAPEntry entry = DAAPEntry.parseStream(in, helper.types);

			for (DAAPEntry e : entry) {
				if (e.getName() == DAAPUtilities.stringToInt("musr")) {
					return (Integer) e.getValue();
				}
			}
			return -1;
		} catch (IOException e) {
			return -1;
		}finally{
			helper.release(in);
		}
	}

	private int getDatabaseId() {
		InputStream in = null;


		if(revisionNumber < 0) return -1;

		String request = "databases?session-id=" + sessionID + "&revision-id="
		+ revisionNumber;


		try {
			in = helper.request(hostname, request, log);
			DAAPEntry entry = DAAPEntry.parseStream(in, helper.types);

			for (DAAPEntry e : entry) {
				if (e.getName() == DAAPUtilities.stringToInt("mlcl")) {
					entry = e;
					break;
				}
			}

			for (DAAPEntry e : entry) {
				if (e.getName() == DAAPUtilities.stringToInt("mlit")) {
					entry = e;
					break;
				}
			}

			for (DAAPEntry e : entry) {
				if (e.getName() == DAAPUtilities.stringToInt("miid")) {
					entry = e;
					return (Integer) e.getValue();
				}
			}

			return -1;

		} catch (IOException e) {
			return -1;
		}finally{
			helper.release(in);
		}

	}
	
	public class ClientExpiredException extends Throwable{
		
	}

}
