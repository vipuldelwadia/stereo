package daccpserver.command;

import dacpserver.DACPServerListener;

public class Play implements DACPServerCommandInterface {

	public void doAction(DACPServerListener s) {
		s.play();
	}

	public String toCommandString() {
		
		return "playpause";
	}

}
