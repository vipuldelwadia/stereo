package player;

import java.io.IOException;
import java.io.InputStream;

import music.Track;
import daap.DaapClient;

public class PlayerTest {

	public static void main(String args[]) {
		String hostname = "majoribanks.mcs.vuw.ac.nz";
		int port = 3689;

		testPlayer(PlayerTest.class.getResourceAsStream("music.mp3"));

		try{
			DaapClient client = new DaapClient(hostname, port);
			testPlayer(client.getTrackList().get(34).getStream());
		}catch(IOException e){
			e.printStackTrace();
		}
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
}
