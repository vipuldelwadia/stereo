package player;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import music.DJ;

import javazoom.jl.decoder.JavaLayerException;

public class Player implements music.Player {

	public void pause() {
		if (thread != null)
			thread.suspend();
	}

	public void setInputStream(InputStream i) {

		if (thread != null) {
			stop();
		}
			
		thread = new TrackThread(i);

		start();
	}

	public void start() {
		if (thread == null);
		else if (thread.isAlive()) {
			thread.resume();
		}
		else {
			thread.start();
		}
	}

	public void stop() {
		if (thread != null){
			thread.close();
			thread.resume();
			thread = null;
		}
	}
	
	private void trackFinished() {
		for (PlaybackListener l: listeners){
			l.playbackFinished();
		}
	}
	
	private void trackStarted() {
		for (PlaybackListener l: listeners){
			l.playbackStarted();
		}
	}


	private TrackThread thread;

	private class TrackThread extends Thread {

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
			trackStarted();
			try {
				player.play();
			}
			catch (JavaLayerException ex) {
				ex.printStackTrace();
			}
			
			trackFinished();
		}

		public void close() {
			player.close();
		}
	}

	public void addPlaybackListener(PlaybackListener l) {
		listeners.add(l);
	}

	public void removePlaybackListener(PlaybackListener l) {
		listeners.remove(l);
	}
	
	private Set<PlaybackListener> listeners = new HashSet<PlaybackListener>();
}
