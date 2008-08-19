package player;

import java.io.InputStream;

import javazoom.jl.decoder.JavaLayerException;

public class Player implements music.Player {

	public void pause() {
		thread.suspend();
	}

	public void setInputStream(InputStream i) {
		thread = new TrackThread(i);
		
		start();
	}

	public void start() {
		if (thread.isAlive()) {
			thread.resume();
		}
		else {
			thread.start();
		}
	}

	public void stop() {
		thread.close();
		thread.resume();
	}
    
	private TrackThread thread;
	
	private static class TrackThread extends Thread {
		
		private javazoom.jl.player.advanced.AdvancedPlayer player;
		
		public TrackThread(InputStream stream) {
			try {
				player = new javazoom.jl.player.advanced.AdvancedPlayer(stream);
			}
			catch (JavaLayerException ex) {
				ex.printStackTrace();
				player = null;
			}
			
		}
		
		public void run() {
			try {
				player.play();
			}
			catch (JavaLayerException ex) {
				ex.printStackTrace();
			}
		}
		
		public void close() {
			player.close();
		}
	}
    
}
