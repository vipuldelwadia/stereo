package daccpclient;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DACPClientParser{
 
    private static DACPClientParser instance;
    
    private DACPClientParser(){
        // Do Nothing
    }
    
    public DACPEntry parseStream(InputStream stream) throws IOException {
        BufferedInputStream bufferedStream = new BufferedInputStream(stream);
        int name = readInteger(bufferedStream);
        int datalength = readInteger(bufferedStream);
        
        DACPEntry entry = new DACPEntry(name, datalength);
        return entry;
    }
    
    private byte[] read(BufferedInputStream bufferedStream, int length) throws IOException {
        
        byte[] buffer = new byte[length];
        int read = 0;
        while (read < length) {
            read += bufferedStream.read(buffer, read, length - read);
        }
        
        return buffer;
    }
    
    private int readInteger(BufferedInputStream bufferedStream) throws IOException {
        
        byte[] buffer = read(bufferedStream, 4);
        return parseInteger(buffer);
    }
    
    private int parseInteger(byte[] buffer) {
        
        if (buffer.length != 4)
            return 0;
        
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (buffer[i] & 0x000000FF) << shift;
        }
        return value;
    }
    
    public static DACPClientParser getInstance(){
        if (instance == null){
            instance = new DACPClientParser();
        }
        return instance;
    }
}