package util.command.ctrlint;

import interfaces.DJInterface;
import interfaces.PlaybackControl;
import interfaces.PlaybackQueue;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import music.Track;
import notification.PlaybackListener;
import util.command.Command;
import util.node.Node;
import dacp.DACPTreeBuilder;

public class PlayStatusUpdate implements Command, PlaybackListener {

	private int revision;
	
	public void init(Map<String, String> args) {
		if (args.containsKey("revision-number")) {
			revision = Integer.parseInt(args.get("revision-number"));
		}
		else {
			revision = 0;
		}
	}

	public Node run(DJInterface dj) {
		
		PlaybackControl control = dj.playbackControl();
		
		if (this.revision >= control.revision()) {
			
			control.registerListener(this);

			try {
				synchronized (this) {
					this.wait(60000); //wait 60 seconds if nothing has changed
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			control.removeListener(this);
		}
		
		Track current = dj.playbackStatus().current();
		interfaces.collection.Collection<? extends Track> playlist = dj.playbackStatus().playlist(); 
		int position = dj.playbackStatus().position();
		byte state = dj.playbackStatus().state();
		int revision = dj.playbackControl().revision();
		int elapsed = dj.playbackStatus().elapsedTime();
		
		try {
			return DACPTreeBuilder.buildPlayStatusUpdate(revision, state,
				(byte)0, (byte)0, current, playlist.id(), position, elapsed);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	public void trackChanged(Track t) {
		synchronized (this) {
			this.notify();
		}
	}

	public void queueChanged(PlaybackQueue queue) {
		synchronized (this) {
			this.notify();
		}
	}

	public void stateChanged(byte state) {
		synchronized (this) {
			this.notify();
		}
	}

}