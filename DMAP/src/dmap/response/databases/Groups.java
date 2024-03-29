package dmap.response.databases;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import api.Constants;
import api.Node;
import api.Reader;
import api.Response;
import api.Writer;
import api.tracks.Track.TrackFactory;
import dmap.node.AlbumNode;
import dmap.node.TrackNode;
import dmap.node.AlbumNode.AlbumFactory;

public class Groups<Type extends Node> extends Response implements Iterable<Type> {
	
	public List<Type> nodes;
	
	public static Groups<AlbumNode> read(Reader reader, AlbumFactory factory) {
		
		List<AlbumNode> nodes = new ArrayList<AlbumNode>();
		
		for (Constants c: reader) {
			switch (c) {
			case dmap_listing:
				Reader list = reader.nextComposite(c);
				for (Constants i: list) {
					switch (i) {
					case dmap_listingitem:
						nodes.add(AlbumNode.read(list.nextComposite(i), factory));
					}
				}
			}
		}
			
		return new Groups<AlbumNode>(Constants.daap_albumgrouping, nodes);
	}
	
	public static Groups<TrackNode> read(Reader reader, TrackFactory factory) {
		
		List<TrackNode> nodes = new ArrayList<TrackNode>();
		
		for (Constants c: reader) {
			switch (c) {
			case dmap_listing:
				Reader list = reader.nextComposite(c);
				for (Constants i: list) {
					switch (i) {
					case dmap_listingitem:
						nodes.add(TrackNode.read(list.nextComposite(i), factory));
					}
				}
			}
		}
			
		return new Groups<TrackNode>(Constants.daap_songgrouping, nodes);
	}
	
	public Groups(Constants type, List<Type> nodes) {
		super(type, Response.OK);
		this.nodes = nodes;
	}
	
	public int size() {
		return nodes.size();
	}
	
	public Iterator<Type> iterator() {
		return nodes.iterator();
	}

	public void write(Writer writer) {
		super.write(writer);
		
		writer.appendList(Constants.dmap_listing, (byte)0, nodes);
	}
}
