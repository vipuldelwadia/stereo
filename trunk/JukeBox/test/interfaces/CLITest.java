package interfaces;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;




public class CLITest {
	CLI cli;
	@Before
	public void testInstantiation(){
		cli = new CLI(); 
	}

	@Test
	public void testinput(){
		cli.input("set volume 0");
		cli.input("set volume 10");
		try{
			cli.input("set volume -1");
			assertTrue(false);
		}
		catch(IllegalArgumentException e){}
		try{
			cli.input("set volume 11");
			assertTrue(false);
		}
		catch(IllegalArgumentException e){}

	}
}