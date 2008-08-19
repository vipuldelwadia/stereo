package client;

public class ClientBroadcaster {
	
	public static String play() {
		return "playpause";
	}
	
	public static String pause() {
		return "pause";
	}

	public static String changeVolume(double newVolume) {
		if(newVolume>255 || newVolume <0) throw new IllegalArgumentException("Volume must be between 0~255");
		return "setproperty?dmcp.volume=" + newVolume;
	}
}
