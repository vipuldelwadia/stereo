package utils.node;

public class ByteNode extends Node {
    public final byte value;
    
    public ByteNode(int code, int length, byte value) {
        super(code, length);
        this.value = value;
    }
    
    public String toString() {
        return "<" + super.toString() + " value=\"" + value + "\" />";
    }
}

