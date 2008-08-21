package util.command;


public class DACPSetVolume implements DACPCommand {

	private final double volume;
	
	public DACPSetVolume(double volume) {
		this.volume = volume;
	}

	public String toCommandString() {
		
		return "setvolume=" + volume;
	}
	
	public double getVolume(){
		return volume;
	}

}
