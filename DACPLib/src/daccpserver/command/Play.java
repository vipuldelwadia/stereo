package daccpserver.command;

import dacpserver.ServerListener;

public class Play implements ServerCommandInterface {

	public void doAction(ServerListener s) {
		s.play();
	}

}
