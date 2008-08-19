package daccpserver.command;

import dacpserver.ServerListener;

public class Pause implements ServerCommandInterface {

	public void doAction(ServerListener s) {
		s.play();
	}

}
