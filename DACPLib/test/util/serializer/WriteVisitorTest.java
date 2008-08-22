package util.serializer;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

public class WriteVisitorTest {

	private static PipedOutputStream out;
	private static PipedInputStream in;
	
	private static LengthVisitor len;
	private WriteVisitor visitor;
	
	@BeforeClass
	public static void setUp() throws Exception {
		out = new PipedOutputStream();
		in = new PipedInputStream();
		
		in.connect(out);
		
		len = new LengthVisitor();
	}
	
	@Before
	public void prepare() throws Exception {
		visitor = new WriteVisitor(len, out);
	}

	@Test
	public void testVisitBooleanNode() throws Exception {
		
		Node node = new BooleanNode(5, true);
		visitor.visit(node);
		
		int length = 9;
		
		assertTrue(in.available() == length);
		
		byte[] read = new byte[length];
		byte[] check = new byte[] { 0, 0, 0, 5, 0, 0, 0, 1, 1 };
		in.read(read);
		
		assertTrue(compare(read, check));
	}

	@Test
	public void testVisitByteNode() throws Exception {
		
		Node node = new ByteNode(5, (byte)'c');
		visitor.visit(node);
		
		int length = 9;
		
		assertTrue(in.available() == length);
		
		byte[] read = new byte[length];
		byte[] check = new byte[] { 0, 0, 0, 5, 0, 0, 0, 1, (byte)'c' };
		in.read(read);
		
		assertTrue(compare(read, check));
	}

	@Test
	public void testVisitComposite() throws Exception {
		
		Composite node = new Composite(5);
		
		node.append(new ByteNode(1, (byte)'a'));
		node.append(new ByteNode(2, (byte)'b'));
		node.append(new ByteNode(3, (byte)'c'));
		node.append(new ByteNode(4, (byte)'d'));
		
		visitor.visit(node);
		
		int length = 44;
		
		assertTrue(in.available() == length);
		
		byte[] read = new byte[length];
		byte[] check = new byte[] { 0, 0, 0, 5, 0, 0, 0, 36, 
				0, 0, 0, 1, 0, 0, 0, 1, 'a',
				0, 0, 0, 2, 0, 0, 0, 1, 'b',
				0, 0, 0, 3, 0, 0, 0, 1, 'c',
				0, 0, 0, 4, 0, 0, 0, 1, 'd'
				};
		in.read(read);
		
		assertTrue(compare(read, check));
	}

	@Test
	public void testVisitIntegerNode() throws Exception {
		Node node = new IntegerNode(5, 50);
		visitor.visit(node);
		
		int length = 12;
		
		assertTrue(in.available() == length);
		
		byte[] read = new byte[length];
		byte[] check = new byte[] { 0, 0, 0, 5, 0, 0, 0, 4, 0, 0, 0, 50 };
		in.read(read);
		
		assertTrue(compare(read, check));
	}

	@Test
	public void testVisitLongLongNode() throws Exception {
		Node node = new LongLongNode(5, 1, 2);
		visitor.visit(node);
		
		int length = 24;
		
		assertTrue(in.available() == length);
		
		byte[] read = new byte[length];
		byte[] check = new byte[] { 0, 0, 0, 5, 0, 0, 0, 16, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 2 };
		in.read(read);
		
		assertTrue(compare(read, check));
	}

	@Test
	public void testVisitLongNode() throws Exception {
		Node node = new LongNode(5, 72623859790382856l);
		visitor.visit(node);
		
		System.out.println(Integer.MAX_VALUE);
		
		int length = 16;
		
		assertTrue(in.available() == length);
		
		byte[] read = new byte[length];
		byte[] check = new byte[] { 0, 0, 0, 5, 0, 0, 0, 8, 1, 2, 3, 4, 5, 6, 7, 8 };
		
		in.read(read);
		
		assertTrue(compare(read, check));
	}

	@Test
	public void testVisitStringNode() throws Exception {
		Node node = new StringNode(5, "hello world!");
		visitor.visit(node);
		
		int length = 20;
		
		assertTrue(in.available() == length);
		
		byte[] read = new byte[length];
		byte[] check = new byte[] { 0, 0, 0, 5, 0, 0, 0, 12, 'h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd', '!' };
		in.read(read);
		
		assertTrue(compare(read, check));
	}
	
	public static boolean compare(byte[] from, byte[] to) {
		for (int i = 0; i < from.length; i++) {
			if (from[i] != to[i]) {
				System.out.println(from[i] + " != " + to[i] + " at " + i);
				return false;
			}
		}
		return true;
	}
}
