package util.command.ctrlint;

import interfaces.PlaybackController;

import java.util.Map;

import dacp.DACPTreeBuilder;

import util.command.Command;
import util.node.Node;

public class GetProperty implements Command {

	private Map<String, String> args;
	
	public void init(Map<String, String> args) {
		this.args = args;
	}

	public Node run(PlaybackController dj) {
		
		if (args != null && args.containsKey("properties")) {
			String property = args.get("properties");
			if (property.equals("dmcp.volume")) {
				return DACPTreeBuilder.buildGetVolume(dj.getVolume());
			}
			else {
				throw new IllegalArgumentException("property not understood: "+property);
			}
		}
		else {
			throw new IllegalArgumentException("no property requested: " + args);
		}
		
	}

}
