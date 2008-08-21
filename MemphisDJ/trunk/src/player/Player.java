package player;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import music.DJ;

import javazoom.jl.decoder.JavaLayerException;

public class Player implements music.Player {
	
	public Player() {
		final Player player = this;
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run () {
				player.stop();
			}
		});
	}

	public synchronized void pause() {
		if (thread != null)
			thread.suspend();
	}

	public synchronized void setInputStream(InputStream i) {
		System.out.println("Setting input stream in player");
		if (thread != null) {
			thread.resume();
			thread.close();
			thread = null;
		}
		System.out.println("Making new thread");	
		thread = new TrackThread(i);

		start();
	}

	public synchronized void start() {
		if (thread == null);
		else if (thread.isAlive()) {
			thread.resume();
		}
		else {
			thread.start();
		}
	}

	public synchronized void stop() {
		if (thread != null){
			thread.resume();
			thread.close();
			thread = null;
		}
	}

	private void trackFinished() {
		Thread t = new Thread() {
			public void run() {
				for (PlaybackListener l: listeners)
					l.playbackFinished();
			}
		};
		t.start();
	}

	private void trackStarted() {
		Thread t = new Thread() {
			public void run() {
				for (PlaybackListener l: listeners)
					l.playbackStarted();
			}
		};
		t.start();
	}


	private volatile TrackThread thread;

	private class TrackThread extends Thread {

		private javazoom.jl.player.Player player;
		private volatile boolean stopped = false;

		public TrackThread(InputStream stream) {
			try {
				player = new javazoom.jl.player.Player(stream);
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
				System.out.println("playback stopped with an exception");
				//ex.printStackTrace();
			}
			player.close();

			if (!stopped) trackFinished();
		}

		public void close() {
			stopped = true;
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
