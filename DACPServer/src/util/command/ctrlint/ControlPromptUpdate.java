package util.command.ctrlint;

import interfaces.DJInterface;

import java.util.Map;

import util.command.Command;

import api.Response;

public class ControlPromptUpdate implements Command {

	//private int promptId;
	
	public void init(Map<String, String> args) {
		/*String id = args.get("prompt-id");
		if (id != null) {
			promptId = Integer.parseInt(id);
		}*/
	}

	public Response run(DJInterface dj) {
		return null;
		//return new util.response.ControlPromptUpdate(promptId);
	}
	
}
