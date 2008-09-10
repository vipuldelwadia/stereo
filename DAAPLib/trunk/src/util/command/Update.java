package util.command;

import interfaces.PlaybackController;

import java.util.Map;

import dacp.DACPTreeBuilder;

import util.node.Node;

public class Update implements Command {

	@SuppressWarnings("unused")
	private int version;
	
	public void init(Map<String, String> args) {
		String rev = args.get("revision-number");
		if (rev == null) {
			version = 1;
		}
		else {
			try {
				version = Integer.parseInt(rev);
			}
			catch (NumberFormatException ex) {
				throw new IllegalArgumentException("revision number " + rev + " is not valid");
			}
		}
	}

	public Node run(PlaybackController dj) {
		//TODO use version to check whether update is needed
		return DACPTreeBuilder.buildUpdateResponse(dj.libraryVersion());
	}

	
}
