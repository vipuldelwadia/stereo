package util.node;

public class StringNode extends Node {
    private final String value;
    
    public StringNode(int code, int length, String value) {
        super(code, length);
        this.value = value;
    }
    
    public String toString() {
        return "<" + super.toString() + " value=\"" + value + "\" />";
    }

	public String getValue() {
		return value;
	}
}