package util.node;

public class IntegerNode extends Node {
    private final int value;
    
    public IntegerNode(int code, int length, int value) {
        super(code, length);
        this.value = value;
    }
    
    public String toString() {
        return "<" + super.toString() + " value=\"" + value + "\" />";
    }

	public int getValue() {
		return value;
	}
}