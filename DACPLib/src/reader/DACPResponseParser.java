package reader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import util.DACPConstants;
import util.node.BooleanNode;
import util.node.ByteNode;
import util.node.Composite;
import util.node.IntegerNode;
import util.node.LengthVisitor;
import util.node.LongLongNode;
import util.node.LongNode;
import util.node.Node;
import util.node.StringNode;
import daap.DAAPConstants;


public class DACPResponseParser {
   private Handler reply;
   private LengthVisitor len;
    
    public DACPResponseParser() {    	
    	reply = new Handler();
    	
    	addStatusUpdate(reply);
    	addPlaylistRequest(reply);
    	
    	len = new LengthVisitor();
    }
    
    private InputStream stream;
    
    public Composite parse(InputStream stream) {
    	this.stream = stream;

        int c = readInteger(stream);
        int b = readInteger(stream);
        Node tree = this.reply.visit(c, b);
        
        return (Composite) tree;
    }
    
    public void addStatusUpdate(Handler reply) {
        reply.register(DACPConstants.mstt, new IntegerNodeHandler());
        reply.register(DACPConstants.cmsr, new IntegerNodeHandler());
        reply.register(DACPConstants.caps, new ShortNodeHandler());
        reply.register(DACPConstants.cash, new BooleanNodeHandler());
        reply.register(DACPConstants.carp, new BooleanNodeHandler());
        reply.register(DACPConstants.caas, new IntegerNodeHandler());
        reply.register(DACPConstants.caar, new IntegerNodeHandler());
        reply.register(DACPConstants.canp, new LongLongNodeHandler());
        reply.register(DACPConstants.cann, new StringNodeHandler());
        reply.register(DACPConstants.cana, new StringNodeHandler());
        reply.register(DACPConstants.canl, new StringNodeHandler());
        reply.register(DACPConstants.cang, new StringNodeHandler());
        reply.register(DACPConstants.asai, new LongNodeHandler());
        reply.register(DACPConstants.cmmk, new IntegerNodeHandler());
        reply.register(DACPConstants.cant, new IntegerNodeHandler());
        reply.register(DACPConstants.cast, new IntegerNodeHandler());
    }
    
    public void addPlaylistRequest(Handler reply) {
        reply.register(DACPConstants.mstt, new IntegerNodeHandler());
        reply.register(DACPConstants.muty, new IntegerNodeHandler());
        reply.register(DACPConstants.mtco, new IntegerNodeHandler());
        reply.register(DACPConstants.mrco, new IntegerNodeHandler());
        
        Handler playlistHandler = new Handler();
        reply.register(DACPConstants.mlcl, playlistHandler);
        
        Handler trackHandler = new Handler();
        playlistHandler.register(DACPConstants.mlit, trackHandler);
        
        trackHandler.register(DAAPConstants.ITEM_KIND, new ByteNodeHandler());
        trackHandler.register(DAAPConstants.TRACK_ID, new IntegerNodeHandler()); 
        trackHandler.register(DAAPConstants.NAME, new StringNodeHandler());
        trackHandler.register(DAAPConstants.ARTIST, new StringNodeHandler());
        trackHandler.register(DAAPConstants.ALBUM, new StringNodeHandler());
        trackHandler.register(DAAPConstants.BITRATE, new ShortNodeHandler());
        trackHandler.register(DAAPConstants.COMPOSER, new StringNodeHandler());
        trackHandler.register(DAAPConstants.GENRE, new StringNodeHandler());
        trackHandler.register(DAAPConstants.SONG_TIME, new IntegerNodeHandler());
        trackHandler.register(DAAPConstants.START_TIME, new IntegerNodeHandler());
        trackHandler.register(DAAPConstants.STOP_TIME, new IntegerNodeHandler());
        
    }
    
    private class Handler {
        public Node visit(int code, int bytes) {
            
            Composite node = new Composite(code);
            
            int read = 0;
            while (read < bytes) {
                int c = readInteger(stream);
                int b = readInteger(stream);
                read += 8;
                
                if (handlers.get(c) == null) {
                	System.err.println("unexpected " + Node.intToCode(c));
                	read += b;
                	try {
						stream.skip(b);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }

                else {
                	 Node n = handlers.get(c).visit(c, b);
                     read += len.visit(n);
                     node.append(n);
                }

            }
            
            return node;
        }
        
        public void register(int code, Handler handler) {
            this.handlers.put(code, handler);
        }
        
        private Map<Integer, Handler> handlers = new HashMap<Integer, Handler>();
    }
    
    private class StringNodeHandler extends Handler {
        public Node visit(int code, int bytes) {
            
            byte[] b = new byte[bytes];
            try {
                stream.read(b);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            String value = new Scanner(new ByteArrayInputStream(b)).next();
            StringNode node = new StringNode(code,value);
            
            return node;
        }
    }
    
    private class IntegerNodeHandler extends Handler {
        public Node visit(int code, int bytes) {
            
            int value = readInteger(stream);
            Node node = new IntegerNode(code, value);
            
            return node;
        }
    }
    
    private class ShortNodeHandler extends Handler {
        public Node visit(int code, int bytes) {
            
            int value = readShort(stream);
            Node node = new IntegerNode(code, value);
            
            return node;
        }
    }
    
    private class BooleanNodeHandler extends Handler {
        public Node visit(int code, int bytes) {
            
            boolean value = readBoolean(stream) == 1;
            Node node = new BooleanNode(code, value);
            
            return node;
        }
    }
    
    private class ByteNodeHandler extends Handler {
        public Node visit(int code, int bytes) {
            
            byte value = readByte(stream);
            Node node = new ByteNode(code, value);
            
            return node;
        }
    }
    
    private class LongNodeHandler extends Handler {
        public Node visit(int code, int bytes) {
            
            long value = readLong(stream);
            Node node = new LongNode(code, value);
            
            return node;
        }
    }
    
    private class LongLongNodeHandler extends Handler {
        public Node visit(int code, int bytes) {
            
            long value = readLong(stream);
            long value2 = readLong(stream);
            Node node = new LongLongNode(code, value, value2);
            
            return node;
        }
    }
    


    
    public static InputStream request(String host, int port, String request) throws IOException, UnknownHostException {
        
        Socket socket = new Socket(host, port);
        PrintStream out = new PrintStream(socket.getOutputStream());
        out.print("GET " + request + " HTTP/1.1\r\n\r\n");
        InputStream in = socket.getInputStream();
        
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
        if (status != 200 && code.equals("OK"))
            throw new IOException("Error connecting to server: " + status + " " + code);
        
        if (!sc.nextLine().equals(""))
            throw new IOException("Expected empty line after header");
        
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
    
    private static int readInteger(InputStream stream) {
        byte[] b = new byte[4];
        try {
            stream.read(b);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return readInteger(b);
    }
    
    private static int readShort(InputStream stream) {
        byte[] b = new byte[2];
        try {
            stream.read(b);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return ((b[0] & 255) << 8) + (b[1] & 255);
    }
    
    private static int readBoolean(InputStream stream) {
    	byte[] b = new byte[1];
    	try {
    		stream.read(b);
    	}
        catch (IOException e) {
            e.printStackTrace();
        }
        return ((b[0] & 255));
    }
    
    private static byte readByte(InputStream stream) {
    	byte[] b = new byte[1];
    	try {
    		stream.read(b);
    	}
        catch (IOException e) {
            e.printStackTrace();
        }
        return b[0];
    }
    
    private static int readLong(InputStream stream) {
        byte[] b = new byte[8];
        try {
            stream.read(b);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return readLong(b);
    }
    
    private static int parseInt(String code) {
        return readInteger(code.toCharArray());
    }
    
    private static int readInteger(byte[] b) {
        int size = 0;
        for (int i = 0; i < 4; i++) {
            size <<= 8;
            size += b[i] & 255;
        }
        return size;
    }
    
    private static int readInteger(char[] b) {
        int size = 0;
        for (int i = 0; i < 4; i++) {
            size <<= 8;
            size += b[i] & 255;
        }
        return size;
    }
    
    private static int readLong(byte[] b) {
        int size = 0;
        for (int i = 0; i < 8; i++) {
            size <<= 8;
            size += b[i] & 255;
        }
        return size;
    }
}