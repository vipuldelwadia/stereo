package dacpclient;

import java.math.BigInteger;

public class DACPEntry {
    
    private int name;
    private int datalength;
    
    public DACPEntry(int _name, int _datalength){
        this.name = _name;
        this.datalength = _datalength;
    }
    
    public String getName(){
        
        BigInteger i = BigInteger.valueOf(name);
        i.byteValue();
        
        return "";
        
    }
    
}
