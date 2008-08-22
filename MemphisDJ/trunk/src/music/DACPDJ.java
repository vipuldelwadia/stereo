package music;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import playlist.Track;
import reader.DACPRequestParser;
import util.DACPConstants;
import util.command.DACPCommand;
import util.command.DACPPause;
import util.command.DACPPlay;
import util.command.DACPRequestCurrentSong;
import util.command.DACPRequestPlaylist;
import util.command.DACPSetVolume;

import util.command.DACPSkip;
import util.node.BooleanNode;
import util.node.ByteNode;
import util.node.Composite;
import util.node.IntegerNode;
import util.node.LongNode;
import util.node.Node;
import util.node.StringNode;
import util.serializer.DACPWriter;




public class DACPDJ {

	private final int PORT;

	private final ServerSocket SERVER_SOCK;
	
	private final DJ dj;
	
	public DACPDJ(int port, DJ dj) throws IOException {
		this.PORT = port;
		this.dj = dj;
		SERVER_SOCK = new ServerSocket(PORT);
		System.out.println("Server starting.\n--------\n");
		listen();
	}

	private void listen() throws IOException {
		new Thread(){
			public void run(){
				while (true) {
					System.out.println("Waiting for connection.");
					try {
						new Thread(new ServerRunnable(SERVER_SOCK.accept())).start();
					} catch (IOException e) {}
					System.out.println("Accepting connections.");
				}
			}
		}.start();
	}
	

//
//		DACPDJ s;
//
//		s = new DACPDJ(51234);
//		//s.listen();
//	}
	
	

	private class ServerRunnable implements Runnable {

		private final Socket SOCK;

		private final String responseOKNoBody = "HTTP/1.1 204 OK\r\n\r\n";
		private final String responseOK = "HTTP/1.1 200 OK\r\n\r\n";
		



		public void run() {
			try {
				final PrintStream p = new PrintStream(SOCK.getOutputStream());
				
				new Thread() {
					public void run() {

						try {
							Scanner scan = new Scanner(SOCK.getInputStream());
							while (scan.hasNextLine()){
								String parseText="";
								while(true){
									String current = scan.nextLine();
									if(current.equals("")) break;
									parseText+=current;
								}
								// TODO deal with malformed requests
								DACPCommand s = DACPRequestParser.parse(parseText);
								
								runCommand(s);
								System.out.println();
								

							}
						} catch (IOException e) {
							e.printStackTrace();
						} catch (NoSuchElementException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					
					private void runCommand(DACPCommand s) {
						System.out.println(s);
						if(s instanceof DACPPause){
							dj.pause();
							p.print(responseOKNoBody);
						}else if(s instanceof DACPPlay){
							dj.unpause();
							p.print(responseOKNoBody);
						}else if(s instanceof DACPSkip){
							dj.skip();
							p.print(responseOKNoBody);
						}else if(s instanceof DACPSetVolume){
							dj.setVolume(((DACPSetVolume)s).getVolume());
							p.print(responseOKNoBody);
						}else if(s instanceof DACPRequestCurrentSong){
							Track t = dj.getCurrentTrack();
							//TODO send it
						}else if(s instanceof DACPRequestPlaylist){
							List<Track> pl = dj.getPlaylist();
							Node tree = buildTree(pl);
							
							p.print(responseOK);
							new DACPWriter(tree, p);
						}
				
					}

					private Node buildTree(List<Track> pl) {
						List<Node> tracks = new ArrayList<Node>();
						for(Track t:pl){
							List<Node> tags = new ArrayList<Node>();
							for(Map.Entry<Integer, Object> tag:t.getAllTags()){
								Object tagValue = tag.getValue();
								if(tagValue instanceof Byte){
									tags.add(new ByteNode(tag.getKey(), (Byte)tagValue));
								}else if(tagValue instanceof Boolean){
									tags.add(new BooleanNode(tag.getKey(), (Boolean)tagValue));
								}else if(tagValue instanceof Integer){
									tags.add(new IntegerNode(tag.getKey(), (Integer)tagValue));
								}else if(tagValue instanceof Long){
									tags.add(new LongNode(tag.getKey(), (Long)tagValue));
								}else if(tagValue instanceof String){
									tags.add(new StringNode(tag.getKey(), (String)tagValue));
								}
							}
							Composite trackNode = new Composite(DACPConstants.mlit);
							
							for(Node tag:tags){
								trackNode.append(tag);
							}
							
							tracks.add(trackNode);
						}
						
						Composite mlclNode = new Composite(DACPConstants.mlcl);
						
						for(Node n:tracks){
							mlclNode.append(n);
						}
						
						
						Node msttNode = new IntegerNode(DACPConstants.mstt, 200);
						Node mutyNode = new IntegerNode(DACPConstants.muty, 0);
						Node mtcoNode = new IntegerNode(DACPConstants.mtco, tracks.size());
						Node mrcoNode = new IntegerNode(DACPConstants.mrco, tracks.size());
						
						List<Node> subRootNodes = new ArrayList<Node>();
						subRootNodes.add(msttNode);
						subRootNodes.add(mutyNode);
						subRootNodes.add(mtcoNode);
						subRootNodes.add(mrcoNode);
						subRootNodes.add(mlclNode);
						
						Composite apsoNode = new Composite(DACPConstants.apso);
						
						for(Node n:subRootNodes){
							apsoNode.append(n);
						}
						
						return apsoNode;
					}

					
					

				}.start(); //Override the run method.

				
			}catch (IOException e){
				e.printStackTrace();
			}
		}

		private ServerRunnable(Socket s) {
			this.SOCK = s;
		}

	}
}
