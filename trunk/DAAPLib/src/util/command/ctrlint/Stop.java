package util.command.ctrlint;

import interfaces.DJInterface;

import java.util.Map;

import util.command.Command;
import util.node.Node;

public class Stop implements Command {

	public void init(Map<String, String> args) {
		// no params
	}

	public Node run(DJInterface dj) {
		
		dj.playbackControl().stop();
		
		return null;
	}

	
}
