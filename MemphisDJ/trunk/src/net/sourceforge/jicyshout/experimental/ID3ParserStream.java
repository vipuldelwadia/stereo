package net.sourceforge.jicyshout.experimental;


/*
  jicyshout relased under terms of the lesser GNU public license 
   http://www.gnu.org/licences/licences.html#TOCLGPL
 */

import java.io.*;
import java.net.*;

/** Early effort and finding and parsing ID3 tags.
    @author Chris Adamson, invalidname@mac.com
 */
public class ID3ParserStream extends PushbackInputStream {

    protected byte[] headerBuf = new byte[10];
    protected static int METADATA_UDP_PORT = 9423;

    protected long bytesRead = 0;  //delete all ref's to this soon

    public ID3ParserStream (InputStream in) { super(in); }
    public ID3ParserStream (InputStream in, int size) { super (in,size); }

    public int read() throws IOException {
        int readByte = super.read();
        bytesRead ++;
        // is it "I"?
        if (readByte == 0x49) {
            headerBuf[0] = (byte) readByte;
            super.read (headerBuf, 1, headerBuf.length-1);
            bytesRead += headerBuf.length-1;
            if (scanHeader (headerBuf)) {
                System.out.println ("found ID3 tag at " + 
                                    (bytesRead - 10));
            }
            // push back those bytes
            super.unread (headerBuf, 1, headerBuf.length-1);
        }
        return readByte;
    } // read();

    /** returns true if this is an ID3 header
     */
    protected boolean scanHeader(byte[] header) {
        if ((header[0] != 0x49) ||
            (header[1] != 0x44) ||
            (header[2] != 0x33)) {
            // System.out.println ("Header not \"ID3\"");
            return false;
        }
        if ((header[3] == 0xff) ||
            (header[4] == 0xff)) {
            System.out.println ("Illegal ID3 version");
            return false;
        }
        // bottom 4 bits of flag byte must be clear
        if ((header[5] & 0xf0) != 0) {
            System.out.println ("Illegal flag bits");
            return false;
        }
        // look through flag byte
        boolean unsynchronized = ((header[5] >>> 7) == 1);
        boolean extendedHeader = 
            (((header[5] & 0x40) >>> 6) == 1);
        boolean experimentalTag = 
            (((header[5] & 0x20) >>> 5) == 1);
        boolean footerPresent = 
            (((header[5] & 0x10) >>> 4) == 1);
        System.out.println ("Unsynchronized: " +
                            (unsynchronized ? "true" : "false"));
        System.out.println ("Extended header: " +
                            (extendedHeader ? "true" : "false"));
        System.out.println ("Experimental: " +
                            (experimentalTag ? "true" : "false"));
        System.out.println ("Footer: " +
                            (footerPresent ? "true" : "false"));

        // size (synchsafe)
        // synchsafe's use 7 bits (always leaving top bit 0)
        // so go through array and shift 7 bits left, then 
        // OR in the next byte
        int size = 0;
        for (int i=6; i<=9; i++) {
            size = size << 7;
            size = size | header[i];
        }
        System.out.println ("Size is " + size);

        return true;

    } // scan header

    public static void main (String[] args) {
        if (args.length < 1) {
            System.out.println ("Usage: ID3ParserStream <url>");
            return;
        }
        try {
            /* note: one of these standards gets metadata via
               UDP packets on port set up with
               x-audiocast-udpport: 10000 
             */
            URL url = new URL (args[0]);
            URLConnection conn = url.openConnection();
            // conn.setRequestProperty ("icy-metadata", "1");
            conn.setRequestProperty ("Icy-Metadata", "1");
            conn.setRequestProperty ("x-audiocast-udpport",
                                     Integer.toString (METADATA_UDP_PORT));
            // kick off metadata thread
            Thread t = new UDPMetadataThread (METADATA_UDP_PORT);
            t.start();
            // now get the stream
            ID3ParserStream id3stream =
                new ID3ParserStream (conn.getInputStream(), 32767); // 32k
            FileOutputStream dump = new FileOutputStream (new File ("dump.mp3"));
            System.out.println ("got stream");
            // while (id3stream.available() > -1) {
            for (int i=0; i<100000; i++) {
                dump.write(id3stream.read());
            }
            System.out.println ("out of while");
            dump.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
