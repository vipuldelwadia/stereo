package interfaces;

import java.util.Scanner;

import player.Controller;



public class CLI{
	private Scanner scan;
	private Controller controller;
	
	public CLI(){
		scan = new Scanner(System.in);
		controller = Controller.getInstance();
		run();
	}
	
	public void run(){
		System.out.println("");
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
		input = input.toLowerCase();
		if(input.equals("play")){
			controller.playTrack();
		}
		else if(input.equals("pause")){
			controller.pauseTrack();
		}
		else if(input.equals("skip")){
			controller.skipTrack();
		}
		else if(input.startsWith("set volume ")){
			input = input.substring("set volume ".length());
			try {
				int vol = Integer.parseInt(input);
				if (vol < 0 || vol > 10) {
					invalidInput("volume not between 0 and 10");
				}
				else {					
					controller.changeVolume(vol);
				}
			}
			catch (NumberFormatException e) {
				invalidInput("volume not a valid number");
			}
			
		}
		else if(input.equalsIgnoreCase("get volume ")){
			controller.getVolume();
			//TODO get volume
		}
		else if(input.startsWith("playlist ")){
			input = input.substring("playlist ".length());
			try {
				int tracks = Integer.parseInt(input);
				if (tracks < 0) {
					invalidInput("tracks must not be < 0");
				}
				else {					
					// TODO print tracks
				}
			}
			catch (NumberFormatException e) {
				invalidInput("volume not a valid number");
			}
		}
		else if(input.equalsIgnoreCase("details")){
			//TODO get song details printed
		}
		else invalidInput("invalid input");
	}
	
	private void invalidInput(String message) {
		// TODO print error message
	}
	public static void main(String[] args) {
		new CLI();
	}

}
