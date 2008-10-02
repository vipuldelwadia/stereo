package player;

import interfaces.Album;
import interfaces.Track;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import music.Player;

public class PlayerTest {

	public static void main(String args[]) {

		InputStream in1 = PlayerTest.class.getResourceAsStream("music.mp3");
		InputStream in2 = PlayerTest.class.getResourceAsStream("music.mp3");
		testSkip(in1, in2);
//		try{
//			DAAPClient client = new DAAPClient(hostname, port);
//			testCompletePlay(client.getTrackList().get(1).getStream());
//		}catch(IOException e){
//			e.printStackTrace();
//		}
		
//		testPlayer(PlayerTest.class.getResourceAsStream("music.mp3"));
//
//		try{
//			DaapClient client = new DaapClient(hostname, port);
//			testPlayer(client.getTrackList().get(34).getStream());
//		}catch(IOException e){
//			e.printStackTrace();
//		}
	}
	

	public static void testCompletePlay(InputStream stream) {
		Player player = new Player();
		player.setTrack(new FakeTrack(stream));
	}
	
	
	public static void testPlayer(InputStream stream) {
		Player player = new Player();
		player.setTrack(new FakeTrack(stream));

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) { e.printStackTrace();}


		player.pause();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) { e.printStackTrace();}

		
		player.start();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) { e.printStackTrace();}

		player.stop();
	}
	
	public static void testSkip(InputStream in1, InputStream in2){
		Player player = new Player();
		player.setTrack(new FakeTrack(in1));
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
	
			e.printStackTrace();
		}
		player.setTrack(new FakeTrack(in2));
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
	
			e.printStackTrace();
		}
		player.stop();
		
	}
	
	private static class FakeTrack implements Track {

		private InputStream stream;
		
		public FakeTrack(InputStream stream) {
			this.stream = stream;
		}
		
		public Album getAlbum() {
			// TODO Auto-generated method stub
			return null;
		}

		public InputStream getStream() throws IOException {
			return stream;
		}

		public void setAlbum(Album album) {
			// TODO Auto-generated method stub
			
		}

		public Map<Integer, Object> getAllTags() {
			// TODO Auto-generated method stub
			return null;
		}

		public Object getTag(int tagID) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}