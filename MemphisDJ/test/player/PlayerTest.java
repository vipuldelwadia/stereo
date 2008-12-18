package player;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.UnsupportedAudioFileException;

import music.Player;
import music.Track;

public class PlayerTest {

	public static void main(String args[]) throws Exception {

		String in1 = "music.mp3";
		String in2 = "music.ogg";
		
		//new AudioPlayer(in1).play();
		
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
	

	public static void testCompletePlay(String stream) {
		Player player = new Player();
		player.setTrack(new FakeTrack(stream));
	}
	
	
	public static void testPlayer(String stream) {
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
	
	public static void testSkip(String in1, String in2) throws UnsupportedAudioFileException, IOException {
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
	
	private static class FakeTrack extends Track {

		private String file;
		
		public FakeTrack(String file) {
			super(0, 0);
			this.file = file;
		}
		
		public InputStream getStream() throws IOException {
			return PlayerTest.class.getResourceAsStream(file);
		}
	}
}
