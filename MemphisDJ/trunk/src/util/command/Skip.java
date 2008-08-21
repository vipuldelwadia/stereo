package util.command;

import music.DJ;

public class Skip implements Command {

	public void doAction(DJ dj) {
		dj.skip();

	}

}
