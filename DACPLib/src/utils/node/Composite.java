package utils.node;

import java.util.ArrayList;
import java.util.List;

public class Composite extends Node {
    public Composite(int code, int length) {
        super(code, length);
    }
    
    public void append(Node node) {
        nodes.add(node);
    }
    
    public String toString() {
        String s = "<" + super.toString() + ">";
        for (Node n : nodes) {
            s += "\n  " + n.toString().replace("\n", "\n  ");
        }
        return s + "\n</" + super.toString() + ">";
    }
    
    public List<Node> nodes = new ArrayList<Node>();
}