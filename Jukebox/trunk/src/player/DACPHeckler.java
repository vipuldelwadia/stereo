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

	private static final boolean DEBUG = true;
	
	private final String HOST;
	private final int PORT;
	private final PrintStream p;
	private Socket sock;

	public DACPHeckler(String host, int port) throws UnknownHostException, IOException {
		this.HOST = host;
		this.PORT = port;
		connect();
		this.p = new PrintStream(sock.getOutputStream());
	}

	private void connect() throws UnknownHostException, IOException {
		sock = new Socket(HOST, PORT);
	}
	
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

    private InputStream response() throws IOException, UnknownHostException {
        
        InputStream in = sock.getInputStream();
        
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
        
        String contentType = null, date = null, version = null;
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
        
        if (DEBUG){
        	System.out.println("Code:" + code);
        	System.out.println("Status:" + status);
        	System.out.println("Content Type:" + contentType);
        	System.out.println("Content Length:" + contentLength);
        	System.out.println("Date:" + date);
        	System.out.println("Version:" + version);
        }
        
        if (status == 204)
        	return null;
        else
        	return in;
    }
	
	
	/*
	 * 
	 */
	
	public List<Track> getLibrary() {
		// TODO Auto-generated method stub
		return new ArrayList<Track>(); //empty for now
	}

	public List<Track> getRecentlyPlayedTracks() {
		// TODO Auto-generated method stub
		return new ArrayList<Track>(); //empty for now
	}

	public Track queryCurrentTrack() {
		// TODO Auto-generated method stub
		return null; //empty for now
	}

	public void setPlaylistWithFilter(Map<Integer, String> filter) {
		// TODO Auto-generated method stub
		
	}
	
	public void play() {
		send(DACPRequestGenerator.play());
		GetResponse();
	}
	public void pause() {
		send(DACPRequestGenerator.pause());
		GetResponse();
	}
    
    public void skip(){
    	send(DACPRequestGenerator.skip());
    	GetResponse();
    }
    
	public void setVolume(int newVolume) {
		send(DACPRequestGenerator.changeVolume(newVolume));
		GetResponse();
	}
	
	public int getVolume(){
		//TODO
		GetResponse();
		return 0;
	}
	
	public void setTracks(List<Track> l){
		//TODO
	}
	
	public void stop() {
		// TODO Auto-generated method stub
		
	}
	
	public List<Track> getPlaylistWithFilter(Map<Integer, String> filter) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Track> getTracks(){
		List<Track> tracks = new ArrayList<Track>();
		try {
			//TODO
			Map<String, String> emptyParamters = new HashMap<String, String>();
			send(DACPRequestGenerator.getTracks(emptyParamters));
			InputStream in = response();
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
		
		for (Track t : tracks){
			System.out.println(t);
		}
		
		return tracks;
	}
	
	private void GetResponse(){
		try {
			response();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void send(String command) {
		p.print("GET /ctrl-int/1/" + command + " HTTP/1.1\r\n\r\n");
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
}
