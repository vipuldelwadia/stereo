package interfaces;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Scanner;

import player.Controller;
import playlist.Track;
import controller.ControllerInterface;

public class CLI {
    private Scanner    scan;
    private ControllerInterface controller;
    

    public CLI(ControllerInterface controller) {

        scan = new Scanner(System.in);
        this.controller = controller;
        run();
    }
    
    public CLI(ControllerInterface controller, String args) {
        scan = new Scanner(System.in);
        this.controller = controller;
        input(args);
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
        public void list(String dummy) {
            List<Track> p = controller.getPlaylist();
            for(Track t:p){
                System.out.println(t.toString());
            }
        }
        public void play(String dummy) {
            controller.playTrack();
        	status(null);
        }
        
        public void status(String dummy){
        	controller.status();
        }
        
        public void filter(String param) {
        	//TODO FIX THIS HORRIBLE THING
        	//s=s.replace("_", " ");
        	//String crit = "";
        	System.out.println(param);
        	Scanner s = new Scanner(param);
        	String type,crit;
        	type=s.hasNext()? s.next().trim(): "";
        	crit=s.hasNextLine()? s.nextLine().trim():"";
        	System.out.println("Type: "+type+"|"+crit+"|");
            controller.filter(type, crit);
            status(null);
        }
        
        public void tracklist(String dummy) {
        	System.out.println(controller.getPlaylist().toString());
        }
        
        public void pause(String dummy) { 
            controller.pauseTrack();
            status(null);
        }
        public void skip(String dummy) {
            controller.skipTrack();
            status(null);
        }
        public void set(String command) {
        	command = command.toLowerCase().trim();
        	if (command.startsWith("volume ")) {
        		new Set().volume(command.substring("volume ".length()));
        	}
        }
        public void stop(String dummy){
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
            }catch(IllegalArgumentException ex){
            	System.out.println(ex.getMessage());
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
                        //System.out.println(sc.delimiter());
                        //sc.useDelimiter("[\\p{javaWhitespace}\"]+");
                        //System.out.println(sc.delimiter());
                        //String[] params = new String[m.getParameterTypes().length];
                        //for (int i = 0; i < params.length; i++) {
                        //    if (sc.hasNext()) {
                        //        params[i] = sc.next();
                        //    }
                        //    else {
                        //        System.out.println("wrong parameters");
                        //        return;
                        //    }
                        String params = sc.hasNextLine() ? sc.nextLine() : "";
                        
                        m.invoke(o, params);
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
    	if (args.length == 0) {
    		new CLI(new Controller());
    	}
    	else {
    		String combinedArgs = "";
    		for(String s : args) {
    			combinedArgs += " " + s;
    		}
    		combinedArgs = combinedArgs.trim();
    		System.out.println(combinedArgs);
    		new CLI(new Controller(), combinedArgs);
    	}
    }
    
}
