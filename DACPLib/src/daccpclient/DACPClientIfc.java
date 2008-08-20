package daccpclient;

public interface DACPClientIfc {

	public void play();
	public void pause();
    public void skip();
    public String getXML(String key);

    public void setVolume(double newVolume);
	public void send(String command);
}
