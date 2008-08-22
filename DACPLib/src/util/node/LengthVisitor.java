package util.node;

public class LengthVisitor implements Visitor {

	public int visit(Node node) {
		return node.visit(this);
	}

	public int visitBooleanNode(BooleanNode node) {
		return 1;
	}

	public int visitByteNode(ByteNode node) {
		return 1;
	}

	
	
	public int visitComposite(Composite node) {
		int length = 0;
		for (Node n: node.nodes) {
			length += 8 + this.visit(n);
		}
		return length;
	}

	public int visitIntegerNode(IntegerNode node) {
		return 4;
	}

	public int visitLongLongNode(LongLongNode node) {
		return 16;
	}

	public int visitLongNode(LongNode node) {
		return 8;
	}

	public int visitStringNode(StringNode node) {
		return node.getValue().length();
	}
}
