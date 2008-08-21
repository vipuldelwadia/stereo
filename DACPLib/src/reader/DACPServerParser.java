package reader;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import utils.commands.*;



public class DACPServerParser {


	public static DACPCommand parse(String p) {

		try {
			Scanner request = new Scanner(p);
			String type = request.next();
			String URI = request.next();
			String protocol = request.next();

			Scanner uri = new Scanner(URI);
			uri.useDelimiter("/");

			String root = uri.next();
			if(root.equalsIgnoreCase("ctrl-int")) root = "ctrl_int";

			//The following line is a more efficient version, but Clare won't let me do it :-(
			//System.out.println(root = (root = uri.next()).equalsIgnoreCase("ctrl-int") ? "ctrl_int" : root);

			switch(CONTROL.valueOf(root)){
			case ctrl_int:
				return parseControl(uri);
			case login:break;
			case databases:break;
			case update:break;
			default: break;
			}

			throw new IllegalArgumentException("invalid string");
		}	
		catch (NoSuchElementException e) {
			throw new IllegalArgumentException("invalid string");
		}
	}

	private static DACPCommand parseControl(Scanner uri){
		String database = uri.next();
		uri.useDelimiter("\\?"); /*Split the command from the rest of the variables*/
		String command = uri.next().replaceAll("/", " ").trim();
		Map<String,String> parameters = new HashMap<String,String>();
		// got to check there are parameters
		if (uri.hasNext()) {
			String parameterString=uri.next();

			parameterString = parameterString.replace('=', ' ').replace('&', ' ');

			uri = new Scanner(parameterString);
			while(uri.hasNext()){
				try{
					parameters.put(uri.next(), uri.next());
				}catch(NoSuchElementException e){
					throw new IllegalArgumentException("Invalid parameters.");
				}
			}
		}

//		System.out.println("Command: "+command+"\nparameter: "+parameter);
		/* Get all the commands before the question mark */
		COMMAND c = COMMAND.valueOf(command);

		switch(c) {
		case pause:
			return new Pause();
		case playpause:
			return new Play();
		case setproperty:
			return setProperty(parameters);
		case skip:
			return new Skip();
		default:
			throw new IllegalArgumentException("command is not recognised");
		}

	}

	private static DACPCommand setProperty(Map<String, String> arguments) {
		if (arguments.containsKey("dmcp.volume")) {
			String stringVolume = arguments.get("dmcp.volume");
			try{
				Double volume = Double.parseDouble(stringVolume);	
				
				if(volume>255 || volume <0) throw new IllegalArgumentException("Volume must be between 0~255"); 
				
				return new SetVolume(volume);
			}
			catch(NumberFormatException e) {
				throw new IllegalArgumentException("not a vtrl-intalid volume");
			}
		}
		throw new IllegalArgumentException("not a valid parameter");
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

	private enum COMMAND {
		pause, playpause, setproperty, skip;
	}
}
