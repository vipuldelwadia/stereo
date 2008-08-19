package server.command;

import server.ServerListener;

public class Play implements ServerCommandInterface {

	public void doAction(ServerListener s) {
		s.play();
	}

}
