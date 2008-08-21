package daccpserver.command;

import dacpserver.DACPServerListener;

public class RequestPlaylist implements DACPServerCommandInterface {

	public void doAction(DACPServerListener s) {
		// TODO Auto-generated method stub

	}

	public String toCommandString() {
		return "request?playlist";
	}

}
