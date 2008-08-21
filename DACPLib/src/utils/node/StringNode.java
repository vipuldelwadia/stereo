package utils.node;

public class StringNode extends Node {
    public final String value;
    
    public StringNode(int code, int length, String value) {
        super(code, length);
        this.value = value;
    }
    
    public String toString() {
        return "<" + super.toString() + " value=\"" + value + "\" />";
    }
}