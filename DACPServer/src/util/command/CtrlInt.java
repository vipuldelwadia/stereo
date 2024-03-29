package util.command;

import interfaces.DJInterface;

import java.util.Map;

import util.command.ctrlint.ControlPromptUpdate;
import util.command.ctrlint.Cue;
import util.command.ctrlint.Current;
import util.command.ctrlint.GetProperty;
import util.command.ctrlint.GetSpeakers;
import util.command.ctrlint.NextItem;
import util.command.ctrlint.NowPlayingArtwork;
import util.command.ctrlint.Pause;
import util.command.ctrlint.PlayPause;
import util.command.ctrlint.PlaySpec;
import util.command.ctrlint.PlayStatusUpdate;
import util.command.ctrlint.Playlist;
import util.command.ctrlint.PrevItem;
import util.command.ctrlint.SetProperty;
import util.command.ctrlint.Stop;
import api.Response;

public class CtrlInt extends PathNode implements Command {

	public void init(Map<String, String> args) {
		// no args
	}

	public Response run(DJInterface dj) {
		
		return new dmap.response.CtrlInt(1);
		
	}
	
	public Command pause(int db) {
		return new Pause();
	}
	
	public Command playpause(int db) {
		return new PlayPause();
	}
	
	public Command nextitem(int db) {
		return new NextItem();
	}
	
	public Command previtem(int db) {
		return new PrevItem();
	}
	
	public Command stop(int db) {
		return new Stop();
	}

	public Command getproperty(int db) {
		return new GetProperty();
	}
	
	public Command setproperty(int db) {
		return new SetProperty();
	}
	
	public Command getspeakers(int db) {
		return new GetSpeakers();
	}
	
	public Command playstatusupdate(int db) {
		return new PlayStatusUpdate();
	}
	
	public Command controlpromptupdate(int db) {
		return new ControlPromptUpdate();
	}
	
	public Command nowplayingartwork(int db) {
		return new NowPlayingArtwork();
	}
	
	public Command playlist(int db) {
		return new Playlist();
	}
	
	public Command cue(int db) {
		return new Cue();
	}
	
	public Command playspec(int db) {
		return new PlaySpec();
	}
	
	public Command current(int db) {
		return new Current();
	}
}