package daccpserver.command;

import dacpserver.ServerListener;

public class SetVolume implements ServerCommandInterface {

	private final double volume;
	
	public SetVolume(double volume) {
		this.volume = volume;
	}
	
	public void doAction(ServerListener s) {
		s.setVolume(volume);
	}

}
