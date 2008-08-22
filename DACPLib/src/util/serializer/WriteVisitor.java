package util.serializer;

import java.io.IOException;
import java.io.OutputStream;

import daap.DAAPUtilities;

import util.node.BooleanNode;
import util.node.ByteNode;
import util.node.Composite;
import util.node.IntegerNode;
import util.node.LengthVisitor;
import util.node.LongLongNode;
import util.node.LongNode;
import util.node.Node;
import util.node.StringNode;
import util.node.Visitor;

public class WriteVisitor implements Visitor {
	
	private LengthVisitor len;
	private OutputStream output;

	public WriteVisitor(LengthVisitor len, OutputStream output) {
		this.len = len;
		this.output = output;
	}

	public int visit(Node node) {
		return node.visit(this);
	}

	public int visitBooleanNode(BooleanNode node) {
		int length = len.visit(node);
		DACPWriter.writeNodeHeader(node, length, output);
		byte b = node.getValue()?(byte)1:(byte)0;
		try {
			output.write(b);
		} catch (IOException e) {
			System.err.println("Failed to write boolean");
			//e.printStackTrace();
		}
		return length;
	}

	public int visitByteNode(ByteNode node) {
		int length = len.visit(node);
		DACPWriter.writeNodeHeader(node, length, output);
		try {
			output.write(node.getValue());
		} catch (IOException e) {
			System.err.println("error writing to output stream");
			//e.printStackTrace();
		}
		return length;
	}

	public int visitComposite(Composite node) {
		int length = len.visit(node);
		DACPWriter.writeCode(node.code, output);
		DACPWriter.writeInt(length-8, output);
		System.out.println("sending composite " + DAAPUtilities.intToString(node.code) + " length " + length);
		for (Node n: node.nodes) {
			this.visit(n);
		}
		return length;
	}

	public int visitIntegerNode(IntegerNode node) {
		int length = len.visit(node);
		DACPWriter.writeNodeHeader(node, length, output);
		DACPWriter.writeInt(node.getValue(), output);
		return length;
	}

	public int visitLongLongNode(LongLongNode node) {
		int length = len.visit(node);
		DACPWriter.writeNodeHeader(node, length, output);
		DACPWriter.writeLong(node.getValue(), output);
		DACPWriter.writeLong(node.getValue2(), output);
		return length;
	}

	public int visitLongNode(LongNode node) {
		int length = len.visit(node);
		DACPWriter.writeNodeHeader(node, length, output);
		DACPWriter.writeLong(node.getValue(), output);
		return length;
	}

	public int visitStringNode(StringNode node) {
		int length = len.visit(node);
		DACPWriter.writeNodeHeader(node, length, output);
		DACPWriter.writeString(node.getValue(), output);
		return length;
	}

}