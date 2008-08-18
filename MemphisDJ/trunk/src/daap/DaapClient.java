package daap;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;


import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.HttpURLConnection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SimpleLog;

public class DaapClient {

	private String hostname  =  "majoribanks.mcs.vuw.ac.nz";
	private int dbid;
	private int revisionNumber;
	private Log log ;
	private int sessionID;
	private int port;
	DaapUtilities helper;
	
	public DaapClient(String hostname, int port){
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

	

	private void getSessionID() {
		String loginRequest = "login";
		InputStream in = null;
		try {
			in = helper.request(hostname, loginRequest, log);
			DaapEntry entry = DaapEntry.parseStream(in, helper.types);
			
			for (DaapEntry e: entry) {
				if (e.getName() == DaapUtilities.stringToInt("mlid")) {
					sessionID = (Integer)e.getValue();
					break;
				}
			}		

		} catch (IOException e) {
			e.printStackTrace();
			
		}finally{
			helper.release(in);
		}
	}
	
	private void getRevisionNumber(){
		String request = "update?session-id="+sessionID;
		InputStream in = null;		

		try {
			in = helper.request(hostname, request, log);
			DaapEntry entry = DaapEntry.parseStream(in, helper.types);
			
			for (DaapEntry e: entry) {
				if (e.getName() == DaapUtilities.stringToInt("musr")) {
					revisionNumber = (Integer)e.getValue();
					break;
				}
			}
		
		} catch (IOException e) {
			System.out.println("Died while trying to get the revision number...");
			e.printStackTrace();
		}finally{
			helper.release(in);
		}
		
		
	}
	
	private void getDatabaseId() {
		String request = "databases?session-id="+sessionID+"&revision-id="+revisionNumber;
		InputStream in = null;
		
		try {
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
			
			System.out.println("dbid = "+dbid);
			
			
		} catch (IOException e) {
			System.out.println("Died while getting database id");
			e.printStackTrace();
		}finally{
			helper.release(in);
		}
	}
	
	private void doStuff() throws IOException{

		
		
		HttpClient client = new HttpClient();

		DaapUtilities helper = new DaapUtilities(hostname, log);
		InputStream in = helper.request(hostname, "databases?session-id=21", log);
		
		DaapEntry entry = DaapEntry.parseStream(in, helper.types);
		
		//System.out.printf("%s, %d, %d, %d\n",DaapUtilities.intToString(entry.getName()), entry.getLength(), entry.getNumChildren(), entry.getType(), entry.getValue());
		
		for (DaapEntry e: entry) {
			if (e.getName() == DaapUtilities.stringToInt("mlcl")) {
				entry = e;
				break;
			}
		}

		DaapEntry e = entry.iterator().next();

		Map<Integer, Object> entries = e.getValueMap();

		int id = (Integer)entries.get(DaapUtilities.stringToInt("miid"));
		String name = (String)entries.get(DaapUtilities.stringToInt("minm"));
		int items = (Integer)entries.get(DaapUtilities.stringToInt("mimc"));
		int playlists = (Integer)entries.get(DaapUtilities.stringToInt("mctc"));

		//System.out.println("database " + name + " has " + items + " songs and " + playlists + " playlists.");

		int databaseId = id;
		helper.release(in);
		
		
		
		InputStream stream = helper.request(hostname, "databases/" + databaseId + "/items?type=music&meta=dmap.itemkind,dmap.itemid,dmap.itemname,daap.songalbum,daap.songartist,daap.songgenre,daap.songcomposer,daap.songbitrate,daap.songsamplerate,daap.songtime&session-id=21", log);

		entry = DaapEntry.parseStream(stream, helper.types);

		//System.out.println("daap-client: retrieved data.");

		if ((entry == null) || (entry.getName() != DaapUtilities.stringToInt("adbs"))) {
			//System.out.println(DaapUtilities.intToString(entry.getName()));
			throw new IOException("'" + entry.getName() + "'");
		}
		
		for (DaapEntry e2: entry) {
			if (e2.getName() == DaapUtilities.stringToInt("mlcl")) {
				//System.out.println(DaapUtilities.intToString(e2.getName()));
				entry = e2;
				break;
			}
		}
		/*
		//FileOutputStream fos = new FileOutputStream(new File("filelist.txt"));
		for (DaapEntry e2: entry) {
			if ((entry == null) || !entry.hasChildren()) {
				continue;
			}
			final Map<Integer, Object> values = e2.getValueMap();
			int reference = (Integer)values.get(DaapUtilities.stringToInt("miid"));
			String songName = (String)values.get(helper.stringToInt("minm"));
			System.out.println(reference + " " + songName);
			//fos.write((reference + " " + songName+"\n").getBytes());
			
		}
		*/

		
		
		in = helper.request(hostname, "databases/"+id+"/items/1529529.mp3?session-id=21", log);
		FileOutputStream fos = new FileOutputStream(new File("2.mp3"));
		
		int x;
		while ((x = in.read()) != -1)
			fos.write(x);
		
		fos.close();

		
	}
	

}
