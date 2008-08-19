package player;

import java.io.InputStream;

import javazoom.jl.decoder.JavaLayerError;
import javazoom.jl.decoder.JavaLayerException;

import music.Track;
import daap.DaapClient;

public class Player implements music.Player {

	public void pause() {
		// TODO Auto-generated method stub
		
	}

	public void setInputStream(InputStream i) {
		thread = new TrackThread(i);
		
		start();
	}

	public void start() {
		thread.start();
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}
    
	private TrackThread thread;
	
	private static class TrackThread extends Thread {
		
		private final InputStream stream;
		
		public TrackThread(InputStream stream) {
			this.stream = stream;
		}
		
		public void run() {
			
			try {
				javazoom.jl.player.Player player = new javazoom.jl.player.Player(stream);
				player.play();
			}
			catch (JavaLayerException ex) {
				ex.printStackTrace();
			}
		}
	}
    
}
