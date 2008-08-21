package dacpwriter;

import org.junit.Test;

import playlist.Track;

import daap.DAAPClient;
import daap.DAAPEntry;
import daap.DAAPUtilities;
import static org.junit.Assert.*;


public class DACPEntryTest {
	
	@Test
	public void addChildTest(){
		DACPEntry entry = new DACPEntry();
		assertEquals(0, entry.getNumberChildren());
		entry.addChild(new DACPEntry());
		assertEquals(1, entry.getNumberChildren());
	}
	
	
	@Test
	public void setValueTest(){
		DACPEntry entry = new DACPEntry();
		entry.setValue(new Integer(12));
		Object value = entry.getValue();
		assertTrue(value instanceof Integer);
		assertEquals(12, (Integer)value);
	}
	
	@Test
	public void setNameTest(){
		DACPEntry entry = new DACPEntry();
		entry.setName("apso");
		assertTrue(entry.getName().equals("apso"));
	}
	
	@Test
	public void writeTest(){
		DACPEntry entry = new DACPEntry();
		entry.setValue("apso");
		entry.setValue(new Integer(12));
		String output = entry.write();
		assertTrue(output.equals("apso412"));
		
	}
	
}
