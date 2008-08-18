package interfaces;

import java.util.List;
import java.util.Scanner;

import player.Controller;
import playlist.Track;



public class CLI {
	private Controller c;
	private Scanner scan;
	
	public CLI(){
		scan = new Scanner(System.in);
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
			try {
				c.play();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Playing failed");
				e.printStackTrace();
			}
			//TODO send play command 
		}
		else if(input.equalsIgnoreCase("Pause")){
			try {
				c.pause();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Pausing failed");
				e.printStackTrace();
			}
			//TODO send pause command
		}
		else if(input.equalsIgnoreCase("skip")){
			try {
				c.skip();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("skipping failed");
				e.printStackTrace();
			}
			//TODO send skip command
		}
		else if(input.startsWith("set volume ")){
			input = input.substring(11);
			int vol = Integer.parseInt(input);
			c.setVolume(vol);
			System.out.println("Volume set to " + vol);
		}
		else if(input.equalsIgnoreCase("get volume ")){
			System.out.println("Volume is: " + c.getVolume());
		}
		else if(input.startsWith("playlist ")){
			input = input.substring(9);
			int songs = Integer.parseInt(input);
			List<Track> playlist = c.getList(songs);
			for(Track s: playlist){
				System.out.println(s);
			}
		}
		else if(input.equalsIgnoreCase("back")){
			try {
				c.back();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("changing Track failed");
				e.printStackTrace();
			}
		}
		else if(input.equalsIgnoreCase("details")){
				try {
					Track Track = c.getCurrentSong();
					System.out.println("Track: " + Track.getTitle() );
					System.out.println("Artist: " + Track.getArtist() );
					System.out.println("Album: " + Track.getAlbum() );
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("Track details cannot be returned");
					e.printStackTrace();
				}
			//TODO send previous sohng command
		}
	}

}