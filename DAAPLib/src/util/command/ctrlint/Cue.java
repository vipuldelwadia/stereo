package util.command.ctrlint;

import interfaces.DJInterface;
import interfaces.Track;

import java.util.List;
import java.util.Map;

import util.command.Command;
import util.node.Node;
import util.queryparser.ApplyFilter;
import util.queryparser.Filter;
import util.queryparser.QueryParser;

public class Cue implements Command {

	private Map<String,String> args;
	
	public void init(Map<String, String> args) {
		this.args = args;
	}

	public Node run(DJInterface dj) {

		List<? extends Track> playlist = dj.library().getLibrary();
		
		System.out.println("library has " + playlist.size() + " elements");
		
		if (args == null) {
			return null;
		}
		
		if (args.containsKey("query")) {
		
			Filter f = QueryParser.parser.parse(args.get("query"));
			System.out.println(f);
			playlist = ApplyFilter.filter(f, playlist);
			
			System.out.println("enqueuing " + playlist.size() + " elements");

			dj.playbackControl().enqueue(playlist);
			
		}
		
		if (args.containsKey("index")) {
			
			dj.playbackControl().jump(Integer.parseInt(args.get("index")));
			
		}
		
		if (args.containsKey("command")) {
			
			String cmd = args.get("command");
			
			if (cmd.equals("play")) {
				dj.playbackControl().next();
			}
			else if (cmd.equals("clear")) {
				dj.playbackControl().clear();
			}
			else {
				throw new IllegalArgumentException("unexpected command: " + cmd);
			}
		}

		return null;
	}
	
}
