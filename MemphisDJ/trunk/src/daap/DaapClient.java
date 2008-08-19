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


import music.Track;

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

public class DaapClient {

	private String hostname;
	private int dbid;
	private int revisionNumber;
	private Log log ;
	private int sessionID;
	private int port;
	DaapUtilities helper;
	
	public DaapClient(String hostname, int port) throws IOException{
		//This method should login and get a session ID,
		//the database ID (dbid) by updateing and currentVersion [of database]
		this.hostname = hostname;
		this.port = port;
		
		log = new SimpleLog("Log");
		
		try {
			helper = new DaapUtilities(hostname, log);
		} catch (IOException e) {
			System.out.println("Probably an invalid host");
			e.printStackTrace();
		}
		//Get a session Id
		getSessionID();
		getRevisionNumber();
		getDatabaseId();
		
		
	}

	public List<Track> getTrackList() throws IOException{
		List<Track> tracks = new ArrayList<Track>();
		String request  = "databases/"+ dbid +"/items?type=music&meta=dmap.itemkind,dmap.itemid,dmap.itemname,daap.songalbum,daap.songartist,daap.songgenre,daap.songcomposer,daap.songbitrate,daap.songsamplerate,daap.songstarttime,daap.songstoptime,daap.songtime&session-id="
		+sessionID+"&revision-id="+revisionNumber;
		
		InputStream in = helper.request(hostname, request, log);
		DaapEntry entry = DaapEntry.parseStream(in, helper.types);
	

		if ((entry == null) || (entry.getName() != DaapUtilities.stringToInt("adbs"))) {
			throw new IOException("Failed in getting track list");
		}
		
		for (DaapEntry e: entry) {
			if (e.getName() == DaapUtilities.stringToInt("mlcl")) {
				entry = e;
				break;
			}
		}
		
		
		for (DaapEntry e: entry) {
			if ((entry == null) || !entry.hasChildren()) {
				continue;
			}
			final Map<Integer, Object> values = e.getValueMap();
			tracks.add(new Track(values,this));
						
		}
			
		helper.release(in);
		
		
		return tracks;
	}
	
	public InputStream getStream(Track track) throws IOException{
		int song = track.getTrackId();
		return helper.request(hostname, "databases/"+dbid+"/items/"+song+".mp3?session-id="+sessionID, log);
		}

	public boolean isAlive(){
		String request = "server-info";
		HttpMethod method = null;
		HttpClient client = null;
		try {
			client = new HttpClient();
			method =new GetMethod("http://" + hostname + ":"+port+"/" + request);
		    
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
		    		new DefaultHttpMethodRetryHandler(0, false));
			
			int statusCode = client.executeMethod(method);

	        if (statusCode/100 != HttpStatus.SC_OK/100) {
	          return false;
	        }
	        
//			InputStream in = method.getResponseBodyAsStream();
//			DaapEntry entry = DaapEntry.parseStream(in, helper.types);
//			if(entry == null) {
//				in.close();
//				return false;
//			}
	        
			return true;
		} catch (Exception e) {
			return false;
		}finally{
			method.releaseConnection();
		}
	}
	
	private void getSessionID() throws IOException {
		String loginRequest = "login";
		InputStream in = null;
		
			in = helper.request(hostname, loginRequest, log);
			DaapEntry entry = DaapEntry.parseStream(in, helper.types);
			
			for (DaapEntry e: entry) {
				if (e.getName() == DaapUtilities.stringToInt("mlid")) {
					sessionID = (Integer)e.getValue();
					break;
				}
			}		

		helper.release(in);
		
	}
	
	private void getRevisionNumber() throws IOException{
		String request = "update?session-id="+sessionID;
		InputStream in = null;		

			in = helper.request(hostname, request, log);
			DaapEntry entry = DaapEntry.parseStream(in, helper.types);
			
			for (DaapEntry e: entry) {
				if (e.getName() == DaapUtilities.stringToInt("musr")) {
					revisionNumber = (Integer)e.getValue();
					break;
				}
			}
			helper.release(in);

		
		
	}
	
	private void getDatabaseId() throws IOException {
		String request = "databases?session-id="+sessionID+"&revision-id="+revisionNumber;
		InputStream in = null;
		
			in = helper.request(hostname, request, log);
			DaapEntry entry = DaapEntry.parseStream(in, helper.types);
			
			for(DaapEntry e:entry){
				if (e.getName() == DaapUtilities.stringToInt("mlcl")) {
					entry = e;
					break;
				}
			}

			for(DaapEntry e:entry){
				if (e.getName() == DaapUtilities.stringToInt("mlit")) {
					entry = e;
					break;
				}
			}
 
			for(DaapEntry e:entry){
				if (e.getName() == DaapUtilities.stringToInt("miid")) {
					entry = e;
					dbid = (Integer)e.getValue();
					break;
				}
			}
			
			
			
			helper.release(in);
		
	}
	
}
