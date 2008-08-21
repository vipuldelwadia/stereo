package dacpclient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import dacpclient.node.BooleanNode;
import dacpclient.node.ByteNode;
import dacpclient.node.Composite;
import dacpclient.node.IntegerNode;
import dacpclient.node.LongLongNode;
import dacpclient.node.LongNode;
import dacpclient.node.Node;
import dacpclient.node.StringNode;

public class DACPClientParser {
    
	public static final int cmst = parseInt("cmst");
	public static final int mstt = parseInt("mstt");
	public static final int cmsr = parseInt("cmsr");
	public static final int caps = parseInt("caps");
	public static final int cash = parseInt("cash");
	public static final int carp = parseInt("carp");
	public static final int caas = parseInt("caas");
	public static final int caar = parseInt("caar");
	public static final int canp = parseInt("canp");
	public static final int cann = parseInt("cann");
	public static final int cana = parseInt("cana");
	public static final int canl = parseInt("canl");
	public static final int cang = parseInt("cang");
	public static final int asai = parseInt("asai");
	public static final int cmmk = parseInt("cmmk");
	public static final int cant = parseInt("cant");
	public static final int cast = parseInt("cast");
	
	public static final int muty = parseInt("muty");
	public static final int mtco = parseInt("mtco");
	public static final int mrco = parseInt("mrco");
	public static final int mlcl = parseInt("mlcl");
	public static final int mlit = parseInt("mlit");
	public static final int mikd = parseInt("mikd");
	public static final int miid = parseInt("miid");
	public static final int minm = parseInt("minm");
	public static final int asar = parseInt("asar");
	public static final int mper = parseInt("mper");
	
    private Handler reply;
	
//    public static void main(String args[]) {
//        
//        String host = null;// "127.0.0.1";
//        int port = 3689;
//        
//        try {
//            new Client().getContentCodes(host, port);
//        }
//        catch (UnknownHostException e) {
//            System.err.println(host + " is not known");
//        }
//        
//    }
    
    public DACPClientParser() {    	
    	reply = new Handler();
    	
    	addStatusUpdate(reply);
    	addPlaylistRequest(reply);
    	
    }
    
    private InputStream stream;
    
    public void parse(InputStream stream) {
    	this.stream = stream;

        int c = readInteger(stream);
        int b = readInteger(stream);
        Node tree = this.reply.visit(c, b);
        
        System.out.println(tree.length);
        for (Node n : ((Composite) tree).nodes) {
        	System.out.println(n);
        }
    }
    
    public void addStatusUpdate(Handler reply) {
        reply.register(mstt, new IntegerNodeHandler());
        reply.register(cmsr, new IntegerNodeHandler());
        reply.register(caps, new ShortNodeHandler());
        reply.register(cash, new BooleanNodeHandler());
        reply.register(carp, new BooleanNodeHandler());
        reply.register(caas, new IntegerNodeHandler());
        reply.register(caar, new IntegerNodeHandler());
        reply.register(canp, new LongLongNodeHandler());
        reply.register(cann, new StringNodeHandler());
        reply.register(cana, new StringNodeHandler());
        reply.register(canl, new StringNodeHandler());
        reply.register(cang, new StringNodeHandler());
        reply.register(asai, new LongNodeHandler());
        reply.register(cmmk, new IntegerNodeHandler());
        reply.register(cant, new IntegerNodeHandler());
        reply.register(cast, new IntegerNodeHandler());
    }
    
    public void addPlaylistRequest(Handler reply) {
        reply.register(mstt, new IntegerNodeHandler());
        reply.register(muty, new IntegerNodeHandler());
        reply.register(mtco, new IntegerNodeHandler());
        reply.register(mrco, new IntegerNodeHandler());
        
        Handler playlistHandler = new Handler();
        reply.register(mlcl, playlistHandler);
        
        Handler trackHandler = new Handler();
        playlistHandler.register(mlit, trackHandler);

        trackHandler.register(mikd, new ByteNodeHandler());
        trackHandler.register(miid, new IntegerNodeHandler());
        trackHandler.register(minm, new StringNodeHandler());
        trackHandler.register(asar, new StringNodeHandler());
        trackHandler.register(mper, new LongNodeHandler());
        
    }
    
    public void getContentCodes(String host, int port) throws UnknownHostException {
        
        try {
            InputStream stream = request(host, port, "/content-codes");
            
            parse(stream);
            
           //   System.out.println(tree);
            
//            for (Node n : ((Composite) tree).nodes) {
//                if (n.code == mdcl) {
//                    Composite cc = (Composite) n;
//                    System.out.printf("%s\t%d\t%s\n", ((StringNode) cc.nodes.get(0)).value, ((IntegerNode) cc.nodes.get(2)).value, ((StringNode) cc.nodes
//                            .get(1)).value);
//                }
//            }
            
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private class Handler {
        public Node visit(int code, int bytes) {
            
            Composite node = new Composite(code, bytes);
            
            int read = 0;
            while (read < bytes) {
                int c = readInteger(stream);
                int b = readInteger(stream);
                read += 8;
                
                if (handlers.get(c) == null) {
                    throw new Error("unexpected " + Node.intToCode(c));
                }
                Node n = handlers.get(c).visit(c, b);
                read += n.length;
                node.append(n);
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
            StringNode node = new StringNode(code, bytes, value);
            
            return node;
        }
    }
    
    private class IntegerNodeHandler extends Handler {
        public Node visit(int code, int bytes) {
            
            int value = readInteger(stream);
            Node node = new IntegerNode(code, bytes, value);
            
            return node;
        }
    }
    
    private class ShortNodeHandler extends Handler {
        public Node visit(int code, int bytes) {
            
            int value = readShort(stream);
            Node node = new IntegerNode(code, bytes, value);
            
            return node;
        }
    }
    
    private class BooleanNodeHandler extends Handler {
        public Node visit(int code, int bytes) {
            
            boolean value = readBoolean(stream) == 1;
            Node node = new BooleanNode(code, bytes, value);
            
            return node;
        }
    }
    
    private class ByteNodeHandler extends Handler {
        public Node visit(int code, int bytes) {
            
            byte value = readByte(stream);
            Node node = new ByteNode(code, bytes, value);
            
            return node;
        }
    }
    
    private class LongNodeHandler extends Handler {
        public Node visit(int code, int bytes) {
            
            long value = readLong(stream);
            Node node = new LongNode(code, bytes, value);
            
            return node;
        }
    }
    
    private class LongLongNodeHandler extends Handler {
        public Node visit(int code, int bytes) {
            
            long value = readLong(stream);
            long value2 = readLong(stream);
            Node node = new LongLongNode(code, bytes, value, value2);
            
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