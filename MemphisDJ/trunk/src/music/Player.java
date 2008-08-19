package music;

import java.io.InputStream;

public class Player {
	
	public void setInputStream(InputStream i){
		System.out.println("Set input");
	}
	public void start(){
		System.out.println("start");
	}
	public void stop(){
		System.out.println("stop");
	}
	public void pause(){
		System.out.println("pause");
	}

}
