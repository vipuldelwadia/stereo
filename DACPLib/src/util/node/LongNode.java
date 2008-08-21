package util.node;

public class LongNode extends Node {
    private final long value;
    
    public LongNode(int code, int length, long value) {
        super(code, length);
        this.value = value;
    }
    
    public String toString() {
        return "<" + super.toString() + " value=\"" + value + "\" />";
    }

	public long getValue() {
		return value;
	}
}