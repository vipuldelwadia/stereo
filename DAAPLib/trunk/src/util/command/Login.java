package util.command;

import java.util.Map;

import dacp.DACPTreeBuilder;
import interfaces.PlaybackController;
import util.node.Node;

public class Login implements Command {

	public void init(Map<String, String> args) {
		System.out.println("logged in with argument: " + args);
	}

	public Node run(PlaybackController dj) {
		
		return DACPTreeBuilder.buildLoginNode();
	}
}