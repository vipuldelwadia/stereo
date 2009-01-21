package util.command.ctrlint;

import interfaces.Constants;
import interfaces.DJInterface;

import java.util.Map;

import util.command.Command;
import api.Response;

public class GetProperty implements Command {

	private Map<String, String> args;
	
	public void init(Map<String, String> args) {
		this.args = args;
	}

	public Response run(DJInterface dj) {
		
		if (args != null && args.containsKey("properties")) {
			String property = args.get("properties");
			if (property.equals("dmcp.volume")) {
				return new util.response.ctrlint.GetProperty(Constants.dmcp_volume, dj.volume().getVolume());
			}
			else {
				throw new IllegalArgumentException("property not understood: "+property);
			}
		}
		else {
			throw new IllegalArgumentException("no property requested: " + args);
		}
		
	}

}
