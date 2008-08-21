package util.command;

import music.DJ;

public class SetVolume implements Command {

	private int volume;
	
	public SetVolume(int volume){
		this.volume = volume;
	}
	
	public void doAction(DJ dj) {
		dj.setVolume(volume);

	}

}
