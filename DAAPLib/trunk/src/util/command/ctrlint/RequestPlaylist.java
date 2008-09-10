package util.command.ctrlint;

import interfaces.PlaybackController;
import interfaces.Track;

import java.util.List;
import java.util.Map;

import dacp.DACPTreeBuilder;
import util.command.Command;
import util.node.Node;

public class RequestPlaylist implements Command {

	public String toString() {
		return "requestplaylist";
	}

	public void init(Map<String, String> args) {
		// no args
	}

	public Node run(PlaybackController dj) {
		
		List<Track> playlist = dj.getPlaylist();
		return DACPTreeBuilder.buildPlaylistResponse(playlist);
	}

}
