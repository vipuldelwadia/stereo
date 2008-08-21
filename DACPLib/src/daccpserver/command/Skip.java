package daccpserver.command;

import dacpserver.DACPServerListener;

public class Skip implements DACPServerCommandInterface {

	public void doAction(DACPServerListener s) {
		s.skip();
	}

	public String toCommandString() {
		
		return "skip";
	}

}
