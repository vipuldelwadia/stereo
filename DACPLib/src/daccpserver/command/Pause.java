package daccpserver.command;

import dacpserver.DACPServerListener;

public class Pause implements DACPServerCommandInterface {

	public void doAction(DACPServerListener s) {
		s.pause();
	}

}
