package util.command.ctrlint;

import interfaces.PlaybackController;
import interfaces.Track;

import java.util.Map;

import dacp.DACPTreeBuilder;

import util.command.Command;
import util.node.Node;

public class PlayStatusUpdate implements Command {

	@SuppressWarnings("unused")
	private int revision;
	
	public void init(Map<String, String> args) {
		if (args.containsKey("revision-number")) {
			revision = Integer.parseInt(args.get("revision-number"));
		}
		else {
			revision = 0;
		}
	}

	public Node run(PlaybackController dj) {
		
		//TODO use revision number to compare with dj revision
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Track current = dj.getCurrentTrack();
		byte state = dj.playbackStatus();
		int revision = dj.playbackRevision();
		int elapsed = dj.playbackTime();
		
		return DACPTreeBuilder.buildPlayStatusUpdate(revision, state,
				(byte)0, (byte)0, current, elapsed);
		
	}

}
