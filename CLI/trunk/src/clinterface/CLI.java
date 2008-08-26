package clinterface;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Scanner;

import player.Controller;
import playlist.Track;
import controller.ControllerInterface;

public class CLI {

	private final static int defaultDJPort = 3689;
	private final static boolean DEBUG = false;

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

        
        public void recent(String dummy) {
            controller.queryRecentlyPlayed();
        }
    
        public void query(String param) {
        	System.out.println(param);
        	Scanner s = new Scanner(param);
        	String type,crit;
        	type=s.hasNext()? s.next().trim(): "";
        	crit=s.hasNextLine()? s.nextLine().trim():"";
        	System.out.println("Query Type: "+type+" with the Criteria of:"+crit+"");
        	controller.queryLibrary(type,crit);
        }
        
        public void status(String dummy){
        	controller.status();
        }
        
        public void library(String dummy){
        	controller.displayLibrary();
        }
        
        public void filter(String param) {
        	Scanner s = new Scanner(param);
        	String type,crit;
        	type=s.hasNext()? s.next().trim(): "";
        	crit=s.hasNextLine()? s.nextLine().trim():"";
        	System.out.println("Filter Type: "+type+" with the Criteria of:"+crit+"");
            controller.createPlaylistWithFilter(type, crit);
            status(null);
        }
        
        public void append(String param) {
        	Scanner s = new Scanner(param);
        	String type,crit;
        	type=s.hasNext()? s.next().trim(): "";
        	crit=s.hasNextLine()? s.nextLine().trim():"";
        	System.out.println("Appended with a new list with Type: "+type+" with the Criteria of:"+crit+"");
            controller.append(type, crit);
            status(null);
        }
        
        public void tracklist(String dummy) {
        	
        	for(Track currentTrack: controller.getPlaylist())
        	System.out.print(currentTrack.toString());
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
            System.out.println("set");
        }
        public void stop(String dummy){
        	System.out.println("Stopped");
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

                        break;
                    }
                }
                if (found == false) {
                    System.out.println("command not found");
                    return;
                }
            }
            catch (Exception ex) {
                System.err.println("error calling method");
//                ex.printStackTrace();
            }
        }
        
        return;        
    }
    
	public static void main(String[] args) {
		// TODO consider getopt
		String location = null;
		Integer port = defaultDJPort;
		String combinedArgs = null;
		try {
			if (args.length >= 1) {
				location = args[0];
			}

			if (args.length >= 2) {
				String portStr = args[1];
				if (!portStr.equals("--")) {
					try {
						port = Integer.parseInt(portStr);
					} catch (NumberFormatException e) {
						System.out.println("Failed parsing port number: " + portStr);
						throw e;
					}
				}
			}

			if (args.length >= 3) {
				combinedArgs = "";
				for (int i = 2; i < args.length; i++) {
					String s = args[i];
					combinedArgs += " " + s;
				}
				combinedArgs = combinedArgs.trim();

				if (DEBUG) {
					System.out.println(combinedArgs);
				}
			}
		} catch (Exception e) {
			usage();
			e.printStackTrace();
			System.exit(-1);
		}

		if (location != null) {
			if (combinedArgs != null) {
				new CLI(new Controller(location, port), combinedArgs);
			} else {
				new CLI(new Controller(location, port));
			}
		} else {
			usage();
		}
	}

	public static void usage() {
		// TODO find a better version of argv[0]
		String appName = "Controller";
		System.out.println("Usage: " + appName + " HOST (PORT | --) [COMMANDS]");
	}
    
}
