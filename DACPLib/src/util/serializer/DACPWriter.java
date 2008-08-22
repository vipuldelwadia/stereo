package util.serializer;

import java.io.IOException;
import java.io.OutputStream;

import util.node.LengthVisitor;
import util.node.Node;

public class DACPWriter {

	private final OutputStream output;
	private final LengthVisitor len;
	
	public DACPWriter(Node tree, OutputStream output) {
		
		len = new LengthVisitor();
		this.output = output;
		
		new WriteVisitor(len, output).visit(tree);
	}	
	
	public static void writeCode(int code, OutputStream out) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte)(code>>24);
		bytes[1] = (byte)((code>>16)%256);
		bytes[2] = (byte)((code>>8)%256);
		bytes[3] = (byte)(code%256);
		try {
			out.write(bytes);
		}
		catch (IOException ex) {
			System.err.println("error writing to output stream");
			ex.printStackTrace();
		}
	}
	
	public static void writeInt(int code, OutputStream out) {
		writeCode(code, out);
	}
	
	public static void writeLong(long code, OutputStream out) {
		writeInt((int)(code>>32), out);
		long base = code & 0xFFFFFFFF;
		writeInt((int)base, out);
	}
	
	public static void writeString(String code, OutputStream out) {
		try {
			char[] chars = code.toCharArray();
			for (char c: chars) {
				out.write((byte)c);
			}
		}
		catch (IOException ex) {
			System.err.println("error writing to output stream");
			ex.printStackTrace();
		}
	}
	
	public static void writeNodeHeader(Node node, int length, OutputStream out){
		writeCode(node.code, out);
		writeInt(length, out);
	}
}
