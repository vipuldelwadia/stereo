package util.node;

public class ByteNode extends Node {
    private final byte value;
    
    public ByteNode(int code, int length, byte value) {
        super(code, length);
        this.value = value;
    }
    
    public String toString() {
        return "<" + super.toString() + " value=\"" + value + "\" />";
    }

	public byte getValue() {
		return value;
	}
}

