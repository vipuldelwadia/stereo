package util.node;

public class BooleanNode extends Node {
    private final boolean value;
    
    public BooleanNode(int code, int length, boolean value) {
        super(code, length);
        this.value = value;
    }
    
    public boolean getValue(){
    	return value;
    }
    
    public String toString() {
        return "<" + super.toString() + " value=\"" + value + "\" />";
    }
}