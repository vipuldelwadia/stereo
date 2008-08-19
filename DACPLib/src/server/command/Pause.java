package server.command;

import server.ServerListener;

public class Pause implements ServerCommandInterface {

	public void doAction(ServerListener s) {
		s.play();
	}

}
