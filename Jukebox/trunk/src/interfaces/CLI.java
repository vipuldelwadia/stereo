package interfaces;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Scanner;

import controller.ControllerInterface;


import player.Controller;
import playlist.Track;

public class CLI {
    private Scanner    scan;
    private ControllerInterface controller;
    

    public CLI(ControllerInterface controller) {

        scan = new Scanner(System.in);
        this.controller = controller;
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
        	List<Track> p = controller.getPlaylist();
            for(Track t:p){
                System.out.println(t.toString());
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
        public void stop(){
        	controller.stop();
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
        new CLI(new Controller());
    }
    
}
