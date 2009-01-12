package util.command.databases;

import interfaces.DJInterface;
import interfaces.collection.Collection;

import java.util.Map;

import music.Track;
import music.UserCollection;
import util.command.Command;
import util.node.Node;
import util.queryparser.QueryParser;
import util.queryparser.Token;
import dacp.DACPTreeBuilder;

/* TODO support playlists:
 * add a playlist
 * : /databases/1/edit?action=add?&edit-params='dmap.itemname:Test' -> medc (mstt 200, miid [playlist id])
 * delete a playlist
 * : /databases/1/edit?action=remove?&edit-params='dmap.itemid:[playlist id]' -> 204 No Content
 * both followed by a reply to update
 * 
 * : /databases/1/containers/[id]/edit?action=add&edit-params='dmap.itemid:[id]' -> need to decode response
 */

public class Edit implements Command {

	private Map<String, String> args;
	
	public void init(Map<String, String> args) {
		this.args = args;
	}

	public Node run(DJInterface dj) {
		
		String action = args.get("action");
		Token params = (Token)QueryParser.parse(args.get("edit-params"));
		
		if (action.equals("add")) {
			
			if (params.property.equals("dmap.itemname")) {
				int id = dj.library().nextCollectionId();
				System.out.printf("creating new collection: %s (%d)\n", params.value, id);
				Collection<? extends Track> c = new UserCollection(params.value, id, params.value.hashCode(), dj.library());
				dj.library().addCollection(c);
				return DACPTreeBuilder.buildNewPlaylistResponse(id);
			}
			else {
				throw new RuntimeException("unknown property: " + params.property);
			}
		}
		else if (action.equals("remove")) {
			int id = Integer.parseInt(params.value);
			
			if (params.property.equals("dmap.itemid")) {
				for (Collection<? extends Track> c: dj.library().collections()) {
					if (c.id() == id) {
						System.out.printf("removing collection: %s (%d)\n", c.name(), id);
						dj.library().removeCollection(c);
					}
				}
				
				return null;
			}
			else {
				throw new RuntimeException("unknown property: " + params.property);
			}
		}
		
		return null;
	}

}