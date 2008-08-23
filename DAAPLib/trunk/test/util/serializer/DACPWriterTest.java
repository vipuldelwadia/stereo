package util.serializer;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.junit.*;


public class DACPWriterTest {
	
	private DACPWriter write;
	
	@BeforeClass
	public void setup(){
		
		PipedOutputStream out = new PipedOutputStream();
		PipedInputStream in = new PipedInputStream();
		
		try {
			in.connect(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void primitiveNodeTest(){
		
	}

}
