package util.command;

import interfaces.DJInterface;

import java.util.Map;

import util.node.Node;
import dacp.DACPTreeBuilder;

public class Update implements Command {

	@SuppressWarnings("unused")
	private int version;
	
	public void init(Map<String, String> args) {
		String rev = args.get("revision-number");
		if (rev == null) {
			version = 1;
		}
		else {
			try {
				version = Integer.parseInt(rev);
			}
			catch (NumberFormatException ex) {
				throw new IllegalArgumentException("revision number " + rev + " is not valid");
			}
		}
	}

	public Node run(DJInterface dj) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODO use version to check whether update is needed
		return DACPTreeBuilder.buildUpdateResponse(dj.libraryVersion());
	}

	
}
