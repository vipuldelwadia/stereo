package util.command;

import music.DJ;

public class Pause implements Command {

	public void doAction(DJ dj) {
		dj.pause();
	}

}
