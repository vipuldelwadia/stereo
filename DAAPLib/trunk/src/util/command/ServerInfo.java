package util.command;

import java.util.Map;

import interfaces.PlaybackController;
import dacp.DACPTreeBuilder;
import util.node.Node;

public class ServerInfo implements Command {

	public void init(Map<String, String> args) {
		//no arguments
	}

	public Node run(PlaybackController dj) {
		System.out.println("Request for server info");

		Node response = DACPTreeBuilder.buildServerInfoNode();

		return response;
	}

}
