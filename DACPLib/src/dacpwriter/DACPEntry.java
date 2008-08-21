package dacpwriter;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import daap.DAAPUtilities;

public class DACPEntry {
    
    private int name;
    private int datalength;
    private Object value;
    private Set<DACPEntry> children;
    
    public DACPEntry(int _name, int _datalength){
        this.name = _name;
        this.datalength = _datalength;
    }

	public DACPEntry() {
		children = new HashSet<DACPEntry>();
	}

	public int getNumberChildren() {
		return children.size();
	}

	public void addChild(DACPEntry child) {
		children.add(child);
	}

	public void setValue(Object obj) {
		this.value = obj;
	}

	public Object getValue() {
		return value;
	}

	public Object getName() {
		return DAAPUtilities.intToString(name);
	}


	public String write() {
		// TODO Auto-generated method stub
		// TODO Really, actullay write me!
		return "apso412";
	}

	public void setName(String name) {
		this.name = DAAPUtilities.stringToInt(name);
		
	}

    
}
