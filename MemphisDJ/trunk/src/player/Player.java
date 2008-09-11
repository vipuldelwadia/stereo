package player;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javazoom.jl.decoder.JavaLayerException;

//TODO: find a way to pause playback which doesn't involve deprecated methods

public class Player implements music.Player {
	
	public Player() {
		/*final Player player = this;
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run () {
				player.stop();
			}
		});*/
	}

	@SuppressWarnings("deprecation")
	public synchronized void pause() {
		if (thread != null) {
			thread.suspend();
			state = PAUSED;
		}
	}

	@SuppressWarnings("deprecation")
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

	@SuppressWarnings("deprecation")
	public synchronized void start() {
		if (thread == null);
		else if (thread.isAlive()) {
			thread.resume();
			state = PLAYING;
		}
		else {
			thread.start();
			state = PLAYING;
		}
	}

	@SuppressWarnings("deprecation")
	public synchronized void stop() {
		if (thread != null){
			thread.resume();
			thread.close();
			thread = null;
			state = STOPPED;
		}
	}
	
	public byte status() {
		return state;
	}
	
	public int elapsed() {
		if (thread != null) {
			return thread.elapsed();
		}
		else {
			return 0;
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
	private volatile byte state;
	
	public static final byte STOPPED = 2;
	public static final byte PAUSED = 3;
	public static final byte PLAYING = 4;

	private class TrackThread extends Thread {

		private javazoom.jl.player.Player player;
		private volatile boolean stopped = false;
		private final InputStream in ;
		private long timestamp;

		public TrackThread(InputStream stream) {
			in = stream;
			try {
				player = new javazoom.jl.player.Player(stream);
			}
			catch (JavaLayerException ex) {
				System.err.println("Funky error with JavaLayer - could not create Player");
				//ex.printStackTrace();
				player.close();
				try {
					in.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
				player = null;
				
			}

		}
		
		public int elapsed() {
			long elapsed = System.currentTimeMillis() - timestamp;
			return (int)elapsed;
		}

		public void run() {
			trackStarted();
			timestamp = System.currentTimeMillis();
			try {
				player.play();
			}
			catch (JavaLayerException ex) {
				System.out.println("playback stopped with an exception");
				//ex.printStackTrace();
			}
			player.close();
			try {
				in.close();
			} catch (IOException e) {
				//e.printStackTrace();
			}
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
