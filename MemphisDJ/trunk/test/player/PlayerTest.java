package player;

import java.io.IOException;
import java.io.InputStream;


import daap.DAAPClient;

public class PlayerTest {

	public static void main(String args[]) {
		String hostname = "majoribanks.mcs.vuw.ac.nz";
		int port = 3689;

		try{
			DAAPClient client = new DAAPClient(hostname,port);
			testSkip(client.getStream(client.getTrackList().get(6)),client.getStream(client.getTrackList().get(8)));
		}catch(IOException e){
			System.out.println("FAILURE");
			e.printStackTrace();
		}
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
		player.setInputStream(stream);
	}
	
	
	public static void testPlayer(InputStream stream) {
		Player player = new Player();
		player.setInputStream(stream);

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
		player.setInputStream(in1);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
	
			e.printStackTrace();
		}
		player.setInputStream(in2);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
	
			e.printStackTrace();
		}
		player.stop();
		
	}
}
