package server.command;

import server.ServerListener;

public class Skip implements ServerCommandInterface {

	public void doAction(ServerListener s) {
		s.skip();
	}

}
