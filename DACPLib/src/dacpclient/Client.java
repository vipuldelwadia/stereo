package dacpclient;

import java.io.ByteArrayInputStream;
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

public class Client {
    
    public static final int mccr = parseInt("mccr");
    public static final int mstt = parseInt("mstt");
    public static final int mdcl = parseInt("mdcl");
    public static final int mcna = parseInt("mcna");
    public static final int mcnm = parseInt("mcnm");
    public static final int mcty = parseInt("mcty");
    
    public static void main(String args[]) {
        
        String host = null;// "127.0.0.1";
        int port = 3689;
        
        try {
            new Client().getContentCodes(host, port);
        }
        catch (UnknownHostException e) {
            System.err.println(host + " is not known");
        }
    }
    
    private InputStream stream;
    
    public void getContentCodes(String host, int port) throws UnknownHostException {
        
        try {
            stream = request(host, port, "/content-codes");
            
            Handler reply = new Handler();
            
            reply.register(mstt, new IntegerNodeHandler());
            
            Handler dict = new Handler();
            reply.register(mdcl, dict);
            
            dict.register(mcna, new StringNodeHandler());
            dict.register(mcnm, new StringNodeHandler());
            dict.register(mcty, new ShortNodeHandler());
            
            int c = readInteger(stream);
            int b = readInteger(stream);
            Node tree = reply.visit(c, b);
            // System.out.println(tree);
            
            for (Node n : ((Composite) tree).nodes) {
                if (n.code == mdcl) {
                    Composite cc = (Composite) n;
                    System.out.printf("%s\t%d\t%s\n", ((StringNode) cc.nodes.get(0)).value, ((IntegerNode) cc.nodes.get(2)).value, ((StringNode) cc.nodes
                            .get(1)).value);
                }
            }
            
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
                    throw new Error("unexpected " + intToCode(code));
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
    
    private class Composite extends Node {
        public Composite(int code, int length) {
            super(code, length);
        }
        
        public void append(Node node) {
            nodes.add(node);
        }
        
        public String toString() {
            String s = "<" + super.toString() + ">";
            for (Node n : nodes) {
                s += "\n  " + n.toString().replace("\n", "\n  ");
            }
            return s + "\n</" + super.toString() + ">";
        }
        
        public List<Node> nodes = new ArrayList<Node>();
    }
    
    private static class Node {
        public final int code;
        public final int length;
        
        public Node(int code, int length) {
            this.code = code;
            this.length = length;
        }
        
        public String toString() {
            return intToCode(code);
        }
    }
    
    private static class StringNode extends Node {
        public final String value;
        
        public StringNode(int code, int length, String value) {
            super(code, length);
            this.value = value;
        }
        
        public String toString() {
            return "<" + super.toString() + " value=\"" + value + "\" />";
        }
    }
    
    private static class IntegerNode extends Node {
        public final int value;
        
        public IntegerNode(int code, int length, int value) {
            super(code, length);
            this.value = value;
        }
        
        public String toString() {
            return "<" + super.toString() + " value=\"" + value + "\" />";
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
    
    private static String intToCode(int code) {
        return "" + (char) (code >> 24) + (char) ((code >> 16) % 256) + (char) ((code >> 8) % 256) + (char) (code % 256);
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
}