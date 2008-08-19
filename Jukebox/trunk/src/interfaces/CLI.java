package src.interfaces;

import java.io.IOException;
import java.util.Scanner;

import src.player.Controller;
import src.playlist.Playlist;
import src.playlist.Song;

public class CLI {
    private Scanner    scan;
    private Controller controller;
    
    public CLI() {
        scan = new Scanner(System.in);
        controller = Controller.getInstance();
        run();
    }
    
    public void run() {
        while (true) {
            System.out.print("> ");
            String input = scan.nextLine().toLowerCase();
            if (input.equals("exit")) {
                scan.close();
                break;
            }
            try {
                input(input);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void input(String input) throws IOException {
        if (input.equals("play")) {
            controller.playTrack();
        }
        else if (input.equals("pause")) {
            controller.pauseTrack();
        }
        else if (input.equals("skip")) {
            controller.skipTrack();
        }
        else if (input.startsWith("set volume ")) {
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
        else if (input.equalsIgnoreCase("get volume ")) {
            controller.getVolume();
            // TODO get volume
        }
        else if (input.startsWith("playlist")) {
            int tracks;
            if (input.equals("playlist")) {
                tracks = -1;
            }
            else {
                input = input.substring("playlist ".length());
                try {
                    tracks = Integer.parseInt(input);
                }
                catch (NumberFormatException e) {
                    tracks = 0;
                }
            }
            Playlist p = controller.getPlaylist();
            for (int i = 0; i < p.size() && (tracks < 0 || i < tracks); i++) {
                Song s = p.getSong(i);
                System.out.println("\t* " + s);
            }
            
        }
        else if (input.equalsIgnoreCase("details")) {
            // TODO get song details printed
        }
        else
            invalidInput("invalid input");
    }
    
    private void invalidInput(String message) {
        System.err.println(message);
    }
    
    public static void main(String[] args) {
        new CLI();
    }
    
}
