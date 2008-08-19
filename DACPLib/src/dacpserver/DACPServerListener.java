package dacpserver;

public interface DACPServerListener {

	public void play();
	
	public void pause();
	
	public void skip();
	
	public void setVolume(double volume);
	
}
