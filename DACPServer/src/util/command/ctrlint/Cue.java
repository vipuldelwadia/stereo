package util.command.ctrlint;

import interfaces.DJInterface;

import java.util.List;
import java.util.Map;

import util.command.Command;
import util.queryparser.ApplyFilter;
import util.queryparser.Filter;
import util.queryparser.QueryParser;
import api.Response;
import api.tracks.Track;

public class Cue implements Command {

	private Map<String,String> args;
	
	public void init(Map<String, String> args) {
		this.args = args;
	}

	public Response run(DJInterface dj) {
		
		if (args == null) return new Response(null, Response.NO_CONTENT);
		
		if (args.containsKey("query")) {
			
			Iterable<? extends Track> playlist = dj.library().tracks();
			System.out.println("library has " + dj.library().size() + " elements");
		
			Filter f = QueryParser.parse(args.get("query"));
			System.out.println(f);
			List<? extends Track > pl = ApplyFilter.filter(f, playlist);
			
			System.out.println("enqueuing " + pl.size() + " elements");

			dj.playbackControl().enqueue(pl);
			
		}
		
		if (args.containsKey("index")) {
			
			dj.playbackControl().jump(Integer.parseInt(args.get("index")));
			
		}
		
		if (args.containsKey("command")) {
			
			String cmd = args.get("command");
			
			if (cmd.equals("play")) {
				dj.playbackControl().next();
				dj.playbackControl().play();
			}
			else if (cmd.equals("clear")) {
				dj.playbackControl().clear();
			}
			else {
				throw new IllegalArgumentException("unexpected command: " + cmd);
			}
		}
		
		return new Response(null, Response.NO_CONTENT);
	}
	
}

