package dacp;

import interfaces.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import daap.DAAPConstants;

import util.DACPConstants;
import util.node.BooleanNode;
import util.node.ByteNode;
import util.node.Composite;
import util.node.IntegerNode;
import util.node.LongLongNode;
import util.node.LongNode;
import util.node.Node;
import util.node.StringNode;
import util.node.VersionNode;

public class DACPTreeBuilder {

	public static Composite buildServerInfoNode() {

		Composite serverInfo = createResponse(DACPConstants.msrv);

		serverInfo.append(new VersionNode(DACPConstants.mpro, new byte[] { 0, 2, 0, 4}));
		serverInfo.append(new VersionNode(DACPConstants.apro, new byte[] { 0, 3, 0, 6}));

		return serverInfo;
	}

	public static Composite buildLoginNode() {

		Composite login = createResponse(DACPConstants.mlog);

		login.append(new IntegerNode(DACPConstants.mlid, 845893608));

		return login;
	}

	public static Composite buildLogoutNode() {

		Composite login = createResponse(DACPConstants.mlog);

		login.append(new IntegerNode(DACPConstants.mlid, 845893608));

		return login;
	}

	public static Composite buildCtrlIntNode() {

		Composite response = createResponse(DACPConstants.apso);
		Composite list = createList(response, DACPConstants.mlcl, 0, 1);

		Composite item = new Composite(DACPConstants.mlit);

		//here's where it gets confusing. What do these codes mean?
		item.append(new IntegerNode(DACPConstants.mlid, 1));
		item.append(new BooleanNode(DACPConstants.cmik, true));
		item.append(new BooleanNode(DACPConstants.cmsp, true));
		item.append(new BooleanNode(DACPConstants.cmsv, true));
		item.append(new BooleanNode(DACPConstants.cass, true));
		item.append(new BooleanNode(DACPConstants.casu, true));

		list.append(item);

		return response;
	}

	public static Composite buildGetVolume(int volume) {

		Composite response = createResponse(DACPConstants.cmgt);

		response.append(new IntegerNode(DACPConstants.cmvo, volume));

		return response;
	}

	public static Composite buildGetSpeakers() {

		Composite response = createResponse(DACPConstants.casp);

		Composite dict = createDictionaryNode();
		response.append(dict);

		dict.append(new LongNode(DACPConstants.msma, 0));
		dict.append(new BooleanNode(DACPConstants.caia, true));
		dict.append(new StringNode(DACPConstants.minm, "Computer"));

		return response;
	}

	public static final byte STOPPED = 2;
	public static final byte PAUSED = 3;
	public static final byte PLAYING = 4;
	
	/**
	 * @param revision
	 * @param playing
	 * @return
	 */
	public static Composite buildPlayStatusUpdate(
			int revision,
			byte status,
			byte shuffle,
			byte repeat,
			Track track,
			int elapsed) {

		Composite response = createResponse(DACPConstants.cmst);
		
		response.append(new IntegerNode(DACPConstants.cmsr, revision)); //revision number
		response.append(new ByteNode(DACPConstants.caps, status)); //playback state
		response.append(new ByteNode(DACPConstants.cash, shuffle)); //shuffle mode
		response.append(new ByteNode(DACPConstants.carp, repeat)); //repeat mode
		
		response.append(new IntegerNode(DACPConstants.caas, 2)); //no idea
		response.append(new IntegerNode(DACPConstants.caar, 6)); //no idea

		if (status != STOPPED && track != null) {
			
			//new  0x0000002a00020b8d00020b9200000773 //new song, same album (different artist, 1 after)
			//new  0x0000002a00020b8d00020b9100000763 //new song, same album (different artist, 3 before)
			//new  0x0000002a0001f0a00001f0a3000006de //new song, same artist different album
			//new  0x0000002a0000f1290000f12f00000d40 //next song in album playing
			//new  0x0000002a0000f1290000f12e00000d3f //next song in album playing
			//new  0x0000002a0000f1290000f12d00000d3e //next song in album playing
			//and  0x0000002a000000000000000000000d3c //paused
			//new  0x0000002a0000f1290000f12c00000d3c //playing new song
			//and  0x0000002a000078240000782700000794 //playing again
			//and  0x0000002a000078240000782700000794 //playing
			//new  0x0000002a000000000000000000000794 //paused
			//now  0x0000002a000000000000000000000f1b //paused
			//was  0x000000280000210f00002df300000f19 //playing
			// 2a (42) is the database number - significant?
			
			//don't know what this is
			response.append(new LongLongNode(DACPConstants.canp,0,0));
			
			String name = (String)track.getTag(DACPConstants.NAME);
			if (name==null) name = "";
			response.append(new StringNode(DACPConstants.cann, name));
			
			String artist = (String)track.getTag(DACPConstants.ARTIST);
			if (artist==null) artist = "";
			response.append(new StringNode(DACPConstants.cana, artist));
			
			String album = (String)track.getTag(DACPConstants.ALBUM);
			if (album==null) album = "";
			response.append(new StringNode(DACPConstants.canl, album));
			
			String genre = (String)track.getTag(DACPConstants.GENRE);
			if (genre==null) genre = "";
			response.append(new StringNode(DACPConstants.cang, genre));
			
			//TODO get the album id
			response.append(new LongNode(DACPConstants.asai, 0x0)); //album id
			response.append(new IntegerNode(DACPConstants.cmmk, 1)); //media kind?
			
			Integer time = (Integer)track.getTag(DACPConstants.SONG_TIME);
			if (time == null) time = new Integer(0);
			response.append(new IntegerNode(DACPConstants.cant, time-elapsed)); //remaining
			response.append(new IntegerNode(DACPConstants.cast, time)); //total
		}

		return response;
	}

	public static Node buildPlaylistResponse(List<Track> playlist) {

		Composite response = createResponse(DACPConstants.apso);

		Composite list = createList(response, DACPConstants.mlcl, 0, playlist.size());

		for (Track t: playlist) {
			list.append(buildTrackNode(t));
		}

		return response;
	}

	public static Node buildControlPromptUpdate(int promptId) {

		Composite response = createResponse(DACPConstants.apso);

		response.append(new IntegerNode(DACPConstants.miid, 0));

		return response;
	}

	public static Node buildUpdateResponse(int version) {

		Composite response = createResponse(DACPConstants.mupd);

		response.append(new IntegerNode(DACPConstants.musr, version));

		return response;
	}

	public static Node buildDatabaseResponse(int items, int containers) {

		Composite response = createResponse(DACPConstants.avdb);

		Composite list = createList(response, DACPConstants.mlcl, 0, 1);

		Composite item = new Composite(DACPConstants.mlit);
		list.append(item);

		item.append(new IntegerNode(DACPConstants.miid, 1));
		item.append(new LongNode(DACPConstants.mper, 0xf35226b7c8ee14d3l)); //this id matches the id in the mdns broadcast
		item.append(new StringNode(DACPConstants.minm, "Memphis Stereo"));
		item.append(new IntegerNode(DACPConstants.mimc, items)); //item count
		item.append(new IntegerNode(DACPConstants.mctc, containers)); //container count

		return response;
	}

	public static Node buildBrowseResponse(int code, List<String> artists) {

		Composite response = createResponse(DACPConstants.abro);

		Composite list = createList(response, code, 0, artists.size());

		for (String s: artists) {
			list.append(new StringNode(DACPConstants.mlit, s));
		}

		return response;
	}

	private static Composite createResponse(int code) {
		Composite response = new Composite(code);
		response.append(new IntegerNode(DACPConstants.mstt, 200));
		return response;
	}

	private static Composite createList(Composite response, int listType, int updateType, int size) {

		response.append(new IntegerNode(DACPConstants.muty, updateType));
		response.append(new IntegerNode(DACPConstants.mtco, size)); //total count
		response.append(new IntegerNode(DACPConstants.mrco, size)); //in this packet count

		Composite list = new Composite(listType);
		response.append(list);

		return list;
	}

	private static Composite buildTrackNode(Track track) {

		List<Node> tags = new ArrayList<Node>();
		Map<Integer, Object> tagMap = track.getAllTags();
		for(int code : tagMap.keySet()){
			Object tagValue = track.getTag(code);
			if(tagValue instanceof Byte){
				tags.add(new ByteNode(code, (Byte)tagValue));
			}else if(tagValue instanceof Boolean){
				tags.add(new BooleanNode(code, (Boolean)tagValue));
			}else if(tagValue instanceof Integer){
				tags.add(new IntegerNode(code, (Integer)tagValue));
			}else if(tagValue instanceof Long){
				tags.add(new LongNode(code, (Long)tagValue));
			}else if(tagValue instanceof String){
				tags.add(new StringNode(code, (String)tagValue));
			}
		}
		Composite trackNode = new Composite(DACPConstants.mlit);

		for(Node tag:tags){
			trackNode.append(tag);
		}

		return trackNode;
	}

	private static Composite createDictionaryNode() {
		return new Composite(DACPConstants.mdcl);
	}
}
