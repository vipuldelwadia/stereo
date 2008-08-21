package util.command;


public class SetVolume implements DACPCommand {

	private final double volume;
	
	public SetVolume(double volume) {
		this.volume = volume;
	}

	public String toCommandString() {
		
		return "setvolume=" + volume;
	}

}
