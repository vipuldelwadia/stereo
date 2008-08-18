package interfaces;

import java.util.List;
import java.util.Scanner;

import player.Controller;
import playlist.PlaylistObserver;
import playlist.Track;



public class CLI{
	private Scanner scan;
	private Controller controller;
	
	public CLI(){
		scan = new Scanner(System.in);
		controller = Controller.getInstance();
	}
	
	public void run(){
		while(true){
			String input = scan.nextLine();
			if(input.equalsIgnoreCase("exit")){
				scan.close();
				break;
			}
			else{
				input(input);
			}
		}
	}
		
	public void input(String input){
		if(input.equalsIgnoreCase("Play")){
			controller.playTrack();
			//TODO send play command 
		}
		else if(input.equalsIgnoreCase("Pause")){
			controller.pauseTrack();
			//TODO send pause command
		}
		else if(input.equalsIgnoreCase("skip")){
			//TODO send skip command
		}
		else if(input.startsWith("set volume ")){
			input = input.substring(11);
			int vol = Integer.parseInt(input);
			controller.changeVolume(vol);
			
		}
		else if(input.equalsIgnoreCase("get volume ")){
			//TODO get volume
		}
		else if(input.startsWith("playlist ")){
			input = input.substring(9);
			int songs = Integer.parseInt(input);
			//TODO Pass the message on
		}
		else if(input.equalsIgnoreCase("details")){
			//TODO get song details printed
		}
	}

}
