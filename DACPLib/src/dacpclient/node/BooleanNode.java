package dacpclient.node;

public class BooleanNode extends Node {
    public final boolean value;
    
    public BooleanNode(int code, int length, boolean value) {
        super(code, length);
        this.value = value;
    }
    
    public String toString() {
        return "<" + super.toString() + " value=\"" + value + "\" />";
    }
}