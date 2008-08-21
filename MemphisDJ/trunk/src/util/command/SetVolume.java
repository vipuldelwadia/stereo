package util.command;

import music.DJ;

public class SetVolume implements Command {

	private double volume;
	
	public SetVolume(double volume){
		this.volume = volume;
	}
	
	public void doAction(DJ dj) {
		dj.setVolume(volume);

	}

}
