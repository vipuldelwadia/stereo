package music;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import daap.DaapClient;
import daap.Handshake;

public class Lackey {
	private Object dj;
	private Handshake hs;
	private List<DaapClient> clients;
	
	
	public Lackey(Object dj){
		this.dj = dj;
		clients = new ArrayList<DaapClient>();
		
		try {
			hs = new Handshake(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("server socket failed to initialise");
			e.printStackTrace();
			System.exit(1);
		}
		
		new Thread(hs).start();
	}
	
//	public void removeTracks(Playlist plist){
//		
//	}
	
	public List<Track> getAllTracks(){
		List<Track> tracks = new ArrayList<Track>();
		
		List<DaapClient> toRemove = new ArrayList<DaapClient>();
		for(DaapClient client:clients){
			try {
				if(client.isAlive()){
					tracks.addAll(client.getTrackList());
				}else{
					toRemove.add(client);
				}
			} catch (IOException e) {
				//swallow
			}
		}
		
		for(DaapClient c:toRemove){
			clients.remove(c);
		}
		
		return tracks;
	}
	
	public List<Track> getSomeTracks(int num){
		if(num < 1) throw new IllegalArgumentException();
		
		List<Track> allTracks = getAllTracks();
		
		if(num >= allTracks.size()){
			return allTracks;
		}
		
		return new ArrayList<Track>(allTracks.subList(0, num));
	}
	
	public synchronized void newConnection(DaapClient newClient){
		if(newClient == null){
			return;
		}
		
		clients.add(newClient);
		
	}
}
