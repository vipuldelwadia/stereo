package player;

import java.io.FileInputStream;

public class PlayerTest {

	public static void main(String args[]) {
		
		Player player = new Player();
		
		player.setInputStream(PlayerTest.class.getResourceAsStream("music.mp3"));
	}
}
