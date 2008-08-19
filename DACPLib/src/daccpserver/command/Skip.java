package daccpserver.command;

import dacpserver.ServerListener;

public class Skip implements ServerCommandInterface {

	public void doAction(ServerListener s) {
		s.skip();
	}

}
