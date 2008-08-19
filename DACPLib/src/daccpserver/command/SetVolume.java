package daccpserver.command;

import dacpserver.DACPServerListener;

public class SetVolume implements DACPServerCommandInterface {

	private final double volume;
	
	public SetVolume(double volume) {
		this.volume = volume;
	}
	
	public void doAction(DACPServerListener s) {
		s.setVolume(volume);
	}

}
