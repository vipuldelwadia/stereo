package dcapdummy;

import music.DJ;
import daccpclient.DACPClientIfc;

public class DACPDummy implements DACPClientIfc{
	private DJ dj; 
	
	public DACPDummy(DJ dj){
		this.dj = dj;
	}

	public String getXML(String key) {
		return null;
	}

	public void pause() {
		dj.pause();
	}

	public void play() {
		dj.play();
	}

	public void send(String command) {
		
	}

	public void setVolume(double newVolume) {
		dj.setVolume(newVolume);
	}

	public void skip() {
		dj.skip();
	}

}
