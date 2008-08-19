package server;

public interface ServerListener {

	public void play();
	
	public void pause();
	
	public void skip();
	
	public void setVolume(double volume);
	
}
