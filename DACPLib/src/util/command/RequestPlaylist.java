package util.command;

public class RequestPlaylist implements DACPCommand {


	public String toCommandString() {
		return "request?playlist";
	}

}
