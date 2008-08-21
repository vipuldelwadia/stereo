package util.command;


public class DACPSetVolume implements DACPCommand {

	private final int volume;
	
	public DACPSetVolume(int volume) {
		this.volume = volume;
	}

	public String toCommandString() {
		
		return "setvolume=" + volume;
	}
	
	public int getVolume(){
		return volume;
	}

}
