package util.command.ctrlint;

import interfaces.PlaybackController;

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
		//TODO get the information from dj to return a proper status update
		return DACPTreeBuilder.buildPlayStatusUpdate();
		
	}

}
