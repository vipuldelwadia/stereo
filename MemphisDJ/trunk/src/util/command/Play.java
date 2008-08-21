package util.command;

import music.DJ;

public class Play implements Command {

	public void doAction(DJ dj) {
		dj.unpause();
	}


}
