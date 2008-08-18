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
	private int currentVersion;
	private Log log = new SimpleLog("Log");
	private int sessionID;
	private int port;

	
	public DaapClient(String hostname, int port){
		//This method should login and get a session ID,
		//the database ID (dbid) by updateing and currentVersion [of database]
		this.hostname = hostname;
		this.port = port;
		HttpClient client = new HttpClient();
		
	}
	
	public void doStuff() throws IOException{

		
		
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
	public static void main(String[] args) throws IOException {
		(new DaapClient()).doStuff();
	}

}
