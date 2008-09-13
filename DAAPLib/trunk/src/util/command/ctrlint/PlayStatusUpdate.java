package util.command.ctrlint;

import interfaces.DJInterface;
import interfaces.PlaybackStatusInterface;
import interfaces.PlaylistStatusListener;
import interfaces.Track;

import java.util.Map;

import util.command.Command;
import util.node.Node;
import dacp.DACPTreeBuilder;

public class PlayStatusUpdate implements Command, PlaylistStatusListener {

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
		
		//TODO use revision number to compare with dj revision
		
		if (this.revision >= dj.playbackRevision()) {
			
			dj.registerPlaybackStatusListener(this);
			
			System.out.println("........waiting for playback status change");
			
			try {
				synchronized (this) {
					this.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("........finished waiting for playback status change");
			
			dj.removePlaybackStatusListener(this);
		}
		
		Track current = dj.currentTrack();
		byte state = dj.playbackStatus();
		int revision = dj.playbackRevision();
		int elapsed = dj.playbackElapsedTime();
		
		return DACPTreeBuilder.buildPlayStatusUpdate(revision, state,
				(byte)0, (byte)0, current, elapsed);
		
	}

	public void currentTrackChanged(PlaybackStatusInterface dj) {
		synchronized (this) {
			this.notify();
		}
	}

}
