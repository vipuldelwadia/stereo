package dacp;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class ServerParser {

		
	public static String parse(String p) throws NoSuchElementException {
		
		Scanner request = new Scanner(p);
		String type = request.next();
		String URI = request.next();
		String protocol = request.next();
		
		System.out.println(type);
		System.out.println(URI);
		System.out.println(protocol);
		
		Scanner uri = new Scanner(URI);
		uri.useDelimiter("/");
		
		String root = uri.next();
		if(root.equalsIgnoreCase("ctrl-int")) root = "ctrl_int";
		System.out.println(root);
		
		//The following line is a more efficient version, but Clare won't let me do it :-(
		//System.out.println(root = (root = uri.next()).equalsIgnoreCase("ctrl-int") ? "ctrl_int" : root);
		
		switch(CONTROL.valueOf(root)){
		case ctrl_int:
			parseControl(uri);
			break;
		case login:break;
		case databases:break;
		case update:break;
		default: break;
		}

		return null;
	}
	
	private static void parseControl(Scanner uri){
		String database = uri.next();
		System.out.println("Database: "+ database);
		uri.useDelimiter("\\?"); /*Split the command from the rest of the variables*/
		String command = uri.next().replaceAll("\\", " ").trim();
				
		System.out.println("Command: "+command);
		
		if(command.equalsIgnoreCase("playpause")) DJ.getInstance().play();
		else if (command.equalsIgnoreCase("pause")) DJ.getInstance().pause();
		
	}
	
	private enum CONTROL { 
		login,databases,update,ctrl_int;
		
		public String toString(){
			switch(this){
				case login: return "login";
				case databases: return "databases";
				case ctrl_int: return "ctrl-int";
				case update: return "update";
				default: return "";
			}
		}
	}
}
