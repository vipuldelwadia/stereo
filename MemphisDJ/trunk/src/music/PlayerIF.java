package music;

import java.io.InputStream;

public interface PlayerIF {
	public void setInputStream(InputStream i);
	public void start();
	public void stop();
	public void pause();
}
