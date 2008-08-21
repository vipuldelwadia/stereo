package util.node;

public class Node {
    public final int code;
    public final int length;
    
    public Node(int code, int length) {
        this.code = code;
        this.length = length;
    }
    
    public static String intToCode(int code) {
        return "" + (char) (code >> 24) + (char) ((code >> 16) % 256) + (char) ((code >> 8) % 256) + (char) (code % 256);
    }

    public String toString() {
        return intToCode(code);
    }
}