package util.node;

public class LongLongNode extends Node {
    public final long value;
    public final long value2;
    
    public LongLongNode(int code, int length, long value, long value2) {
        super(code, length);
        this.value = value;
        this.value2 = value2;
    }
    
    public String toString() {
        return "<" + super.toString() + " value=\"" + value  + " " + value2 + "\" />";
    }
}