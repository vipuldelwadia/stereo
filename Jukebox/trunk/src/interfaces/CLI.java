package src.interfaces;

import java.lang.reflect.Method;
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
            input(input);
        }
    }
    
    private class Top {
        public void list() {
            Playlist p = controller.getPlaylist();
            for(Song s:p){
                System.out.println(s.toString());
            }
        }
        public void play() {
            controller.playTrack();
        }
        public void pause() {
            controller.pauseTrack();
        }
        public void skip() {
            controller.skipTrack();
        }
        public Object set() {
            System.out.println("set");
            return new Set();
        }
    }
    
    private class Set {
        public void volume(String volume) {
            try {
                Integer value = Integer.parseInt(volume);
                controller.changeVolume(value);
            }
            catch (NumberFormatException ex) {
                System.out.println("You were supposed to give me a volume dumbass!");
            }
        }
    }
    
    public void input(String input){
        
        Scanner sc = new Scanner(input);
        
        Object o = new Top();
        
        while (sc.hasNext()) {
            try {
                Method[] methods = o.getClass().getMethods();
                String name = sc.next();
                boolean found = false;
                for (Method m: methods) {
                    if (m.getName().equals(name)) {
                        found = true;
                        String[] params = new String[m.getParameterTypes().length];
                        for (int i = 0; i < params.length; i++) {
                            if (sc.hasNext()) {
                                params[i] = sc.next();
                            }
                            else {
                                System.out.println("wrong parameters");
                                return;
                            }
                        }
                        o = m.invoke(o, params);
                    }
                }
                if (found == false) {
                    System.out.println("method not found");
                    return;
                }
            }
            catch (Exception ex) {
                System.err.println("error calling method");
                ex.printStackTrace();
            }
        }
        
        return;        
    }
    
    public static void main(String[] args) {
        new CLI();
    }
    
}
