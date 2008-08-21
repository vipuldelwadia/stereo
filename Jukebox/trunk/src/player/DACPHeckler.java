package player;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import playlist.Track;
import reader.DACPResponseParser;
import util.node.Composite;
import util.node.Node;
import util.node.StringNode;
import writer.DACPRequestGenerator;
import daap.DAAPConstants;

public class DACPHeckler {

	private final String HOST;
	private final int PORT;
	private final PrintStream p;
	private Socket sock;

	public DACPHeckler(String host, int port) throws UnknownHostException, IOException {
		this.HOST = host;
		this.PORT = port;
		connect();
//		listen();
		this.p = new PrintStream(sock.getOutputStream());
	}

	private void connect() throws UnknownHostException, IOException {
		sock = new Socket(HOST, PORT);
	}
	
//	private void listen() {
//		new Thread() {
//			public void run() {
//				while(true) {
//				try {
//					InputStream in = response();
//					if (in != null) {
//						DACPResponseParser r = new DACPResponseParser();
//						Composite c = r.parse(in);
//						translateResponse(c);
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			}
//		}.start();
//	}
	
	private Object translateResponse(Composite c) {
		String code = Node.intToCode(c.code);
		if ("cmst".equals(code)) {
			Track t = buildTrack(c);
			return t;
		}
		else if ("apso".equals(code)) {
			List<Track> tracks = new ArrayList<Track>();
			for (Node n : c.nodes) {
				if (n instanceof Composite) {
					tracks.add(buildTrack((Composite) n));
				}
			}
			return tracks;
		}
		else return null;
	}

	private Track buildTrack(Composite c) {
		Map<Integer, Object> fields = new HashMap<Integer, Object>();
		
		for(Node node : c.nodes) {
			int code = node.code;
			
			if (DAAPConstants.ALBUM == code && node instanceof StringNode) {
				fields.put(new Integer(DAAPConstants.ALBUM), ((StringNode)node).getValue());
			}
			else if (DAAPConstants.ARTIST == code && node instanceof StringNode) {
				fields.put(new Integer(DAAPConstants.ARTIST), ((StringNode)node).getValue());
			}
			else if (DAAPConstants.BITRATE == code && node instanceof StringNode) {
				fields.put(new Integer(DAAPConstants.BITRATE), ((StringNode)node).getValue());
			}
			else if (DAAPConstants.COMPOSER == code && node instanceof StringNode) {
				fields.put(new Integer(DAAPConstants.COMPOSER), ((StringNode)node).getValue());
			}
			else if (DAAPConstants.GENRE == code && node instanceof StringNode) {
				fields.put(new Integer(DAAPConstants.GENRE), ((StringNode)node).getValue());
			}
			else if (DAAPConstants.NAME == code && node instanceof StringNode) {
				fields.put(new Integer(DAAPConstants.NAME), ((StringNode)node).getValue());
			}
			else if (DAAPConstants.SONG_TIME == code && node instanceof StringNode) {
				fields.put(new Integer(DAAPConstants.SONG_TIME), ((StringNode)node).getValue());
			}
			else if (DAAPConstants.START_TIME == code && node instanceof StringNode) {
				fields.put(new Integer(DAAPConstants.START_TIME), ((StringNode)node).getValue());
			}
			else if (DAAPConstants.STOP_TIME == code && node instanceof StringNode) {
				fields.put(new Integer(DAAPConstants.STOP_TIME), ((StringNode)node).getValue());
			}
		}
		
		return new Track(fields, null);
	}
    

//  String host = null;// "127.0.0.1";
//  int port = 3689;
//  
//  try {
//      new Client().getContentCodes(host, port);
//  }
//  catch (UnknownHostException e) {
//      System.err.println(host + " is not known");
//  }
    
    public InputStream 	response() throws IOException, UnknownHostException {
        
//        Socket socket = new Socket(host, port);
//        PrintStream out = new PrintStream(socket.getOutputStream());
//        out.print("GET " + request + " HTTP/1.1\r\n\r\n");
        InputStream in = sock.getInputStream();
        
        Scanner s = new Scanner(in);
        while(s.hasNext()) System.out.println(s.next());
        
        if(true) return null;
        
        String buffer = "";
        while (true) {
            char c = (char) in.read();
            buffer += c;
            if (c == '\n' && buffer.endsWith("\r\n\r\n"))
                break;
        }
        
        Scanner sc = new Scanner(buffer);
        
        String protocol = sc.next();
        int status = sc.nextInt();
        String code = sc.next();
        
        if (!protocol.equals("HTTP/1.1"))
            throw new IOException("Unsupported Protocol: " + protocol);
        if (status != 200 && status != 204 && code.equals("OK"))
            throw new IOException("Error connecting to server: " + status + " " + code);
        
        if (!sc.nextLine().equals(""))
            throw new IOException("Expected empty line after header");
        
        if (status == 204) return null;
        
        String contentType, date, version = null;
        int contentLength = 0;
        String line;
        
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            if (line.equals(""))
                break;
            
            Scanner lineScanner = new Scanner(line);
            String token = lineScanner.next();
            if (token.equals("Content-Type:")) {
                contentType = lineScanner.next();
            }
            else if (token.equals("Content-Length:")) {
                contentLength = lineScanner.nextInt();
            }
            else if (token.equals("DAAP-Server:")) {
                version = lineScanner.next();
            }
            else if (token.equals("Date:")) {
                date = lineScanner.next();
            }
        }
        
        return in;
    }
	
	public void play() {
		send(DACPRequestGenerator.play());
	}
	public void pause() {
		send(DACPRequestGenerator.pause());
	}
    
    public void skip(){
    	send(DACPRequestGenerator.skip());
    }
    
    public String getXML(String key){
        return null;
    }
	
	public void setVolume(double newVolume) {
		send(DACPRequestGenerator.changeVolume(newVolume));
	}
	
	public int getVolume(){
		//TODO
		return 0;
	}
	
	public List<Track> getTracks(){
		List<Track> tracks = new ArrayList<Track>();
		try {
			send(DACPRequestGenerator.getTracks());
			InputStream in;
			in = response();
			if (in != null) {
				DACPResponseParser r = new DACPResponseParser();
				Composite c = r.parse(in);
				Object o = translateResponse(c);
				if (o != null){
					if (o instanceof List){
						for (Object e : (List)o){
							if (e instanceof Track)
								tracks.add((Track)e);
						}
					}
					else if (o instanceof Track){
						tracks.add((Track)o);
					}
				}
			}
			
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return tracks;
	}
	
	public void setTracks(List<Track> l){
		//TODO
	}
	
	private void send(String command) {
		p.println("GET /ctrl-int/1/" + command + " HTTP/1.1\r\n");
	}

	public static void main(String[] args) throws UnknownHostException, IOException{
		new DACPHeckler("climie", 51234);
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}

}
