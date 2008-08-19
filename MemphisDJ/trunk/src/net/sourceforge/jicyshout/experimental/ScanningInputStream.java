package net.sourceforge.jicyshout.experimental;

/*
  jicyshout relased under terms of the lesser GNU public license 
  http://www.gnu.org/licenses/licenses.html#TOCLGPL
 */


import java.io.*;
import java.net.*;

/** finds first MP3 frame-sync in a stream
    @author Chris Adamson, invalidname@mac.com
 */
public class ScanningInputStream extends InputStream {


    public static final int MPEG1_LAYER1 = 0;
    public static final int MPEG1_LAYER2 = 1;
    public static final int MPEG1_LAYER3 = 2;
    public static final int MPEG2_LAYER1 = 3;
    public static final int MPEG2_LAYER2 = 4;
    public static final int MPEG2_LAYER3 = 5;
    
    /** bitrates as indicated by the high nybble of the
        third header byte.  first index is enc/layer
        (eg, MPEG-1, layer 3), second is the nybble value.
        <p>
        first index is mpeg-1 layers 1-3, then
        mpeg-2 layers 1-3, so a typical mp3 
        (mpeg-1, layer 3) is index 2.
        <p>
        -1 means illegal value (bitrate was 0x00 or 0xff)
       @see http://www.id3.org/mp3frame.html
     */
    public static final int[][] BITRATES = {
        { -1, -1, -1, -1, -1, -1}, // 0 0 0 0
        { 32, 32, 32, 32, 32, 8},  // 0 0 0 1
        { 64, 48, 40, 64, 48, 16}, // 0 0 1 0
        { 96, 56, 48, 96, 56, 24},  // 0 0 1 1
        { 128, 64, 56, 128, 64, 32}, // 0 1 0 0
        { 160, 80, 64, 160, 80, 64}, // 0 1 0 1
        { 192, 96, 80, 192, 96, 80}, // 0 1 1 0
        { 224, 112, 96, 224, 112, 56}, // 0 1 1 1
        { 256, 128, 112, 256, 128, 64}, // 1 0 0 0
        { 288, 160, 128, 288, 160, 128}, // 1 0 0 1
        { 320, 192, 160, 320, 192, 160}, // 1 0 1 0
        { 352, 224, 192, 352, 224, 112}, // 1 0 1 1
        { 384, 256, 224, 384, 256, 128}, // 1 1 0 0
        { 416, 320, 256, 416, 320, 256}, // 1 1 0 1
        { 448, 384, 320, 448, 384, 320}, // 1 1 1 0
        { -1, -1, -1, -1, -1, -1} // 1 1 1 1
    };

    public static final String[] ENCODING_NAMES = {
        "MPEG-1, layer 1",
        "MPEG-1, layer 2",
        "MPEG-1, layer 3",
        "MPEG-2, layer 1",
        "MPEG-2, layer 2",
        "MPEG-2, layer 3"
    };

    // delegates a BufferedInputStream
    // can find frame synch
    // will scan for ID3 tags (and strip them?)

    // why is delegation better than subclassing for this?
    // I forget why I did that

    BufferedInputStream buffy;

    protected long readPoint;

    public ScanningInputStream (BufferedInputStream buffy) {
        super();
        this.buffy = buffy;
        readPoint = 0;
    }

    public int available()
        throws IOException {
        return buffy.available();
    }
    
    public void close()
        throws IOException {
        buffy.close();
    }

    public void mark (int limit) {
        buffy.mark (limit);
    }

    public boolean markSupported () {
        return buffy.markSupported();
    }

    public void reset()
        throws IOException {
        buffy.reset();
    }

    public long skip (long n)
        throws IOException {
        return buffy.skip (n);
    }

    public int read (byte[] b, int off, int len)
        throws IOException {
        return buffy.read (b, off, len);
    }

    public int read (byte[] b)
        throws IOException {
        return buffy.read (b);
    }

    public int read ()
        throws IOException {
        return buffy.read();
    }

    public void cueToFrameSync()
        throws IOException {
        boolean found = false;
        while (! found) {
            buffy.mark (100);
            int byte1 = buffy.read();
            if (byte1 != 0xff) {
                readPoint++;
                continue;
            }
            int byte2 = buffy.read();
            if (((byte2 >>> 4) != 0x0f) &&
                ((byte2 >>> 4) != 0x0e)) {
                readPoint+=2;
                continue;
            }
            System.out.println ("possible frame sync found at " +
                                readPoint);
            int byte3 = buffy.read();
            int byte4 = buffy.read();
            // which encoding? 
            int encoding = getEncodingFromByte2 (byte2);
            System.out.println ("Encoding is " + encoding + " (" +
                                ((encoding == -1 ? "unknown" :
                                  ENCODING_NAMES[encoding])) + ")");
            if (encoding == -1) {
                readAByteAndBail();
                System.out.println ("");
                readPoint+=4;
                continue;
            }
            int bitrateField = ((byte3) & 0xf0) >>> 4;
            System.out.println ("bitrateField is " + bitrateField + " (" +
                                lowNybbleToBinary (bitrateField) + ")");
            int bitrate = BITRATES [bitrateField][encoding];
            System.out.println ("supposed bitrate == " + bitrate);
            if (bitrate == -1) {
                readAByteAndBail();
                System.out.println ("");
                readPoint+=4;
                continue;
            }
            int frequency = getFrameRateFromByte3 (byte3,
                                                   (encoding>2)); //cheez
            System.out.println ("frequency = " + frequency);
            if (frequency == -1) {
                readAByteAndBail();
                readPoint+=4;
                System.out.println ("");
                continue;
            }
            int padBit = (byte3 & 0x02) >>> 1;
            System.out.println ("padBit is " + padBit);
            float bitrateF = (float) bitrate;
            float frequencyF = (float) frequency;
            // int bitrateK = bitrate * 1024;
            int bitrateK = bitrate * 1000;
            // float frameSize = (144f * bitrateF / frequencyF) + padBit;
            int frameSize = (144 * bitrateK / frequency) + padBit;
            System.out.println ("frameSize is " + frameSize);

            if (frameSize > 4) {
                int bytesToSkip = frameSize - 4;
                System.out.println ("looking ahead " + bytesToSkip +
                                    " to " + (readPoint + bytesToSkip));
                buffy.skip (bytesToSkip);
                int nextFrameByte1 = buffy.read();
                if (nextFrameByte1 == 0xff) {
                    System.out.println ("found 0xff!!  probably frame sync!");
                    buffy.reset();
                    return; // this is the frame sync, so return happily
                } else if (nextFrameByte1 == 0x49) {
                    System.out.println ("found 0x49!!  possible id3 tag?");
                    buffy.reset();
                    return; // this is the frame sync, so return happily
                } else {
                    System.out.println ("not 0xff, oh well");
                    readAByteAndBail();
                    readPoint+=4;
                    System.out.println ("");
                    continue;
                }
            } // if frameSize > 4
            // meaningless fall-thru
            readAByteAndBail();
            readPoint+=4;
            System.out.println ("");
        } // while
    }

    /** When cueToFrameSync realizes the supposed header is not
        a header, reset to the marked point, read one byte, and bail.
     */
    public void readAByteAndBail()
        throws IOException {
        buffy.reset();
        buffy.read();
    }


    public int getEncodingFromByte2 (int byte2) {
        int id = (byte2 & 0x08) >>> 3;
        int layer = (byte2 & 0x6) >>> 1;
        // now figure out which const to return
        // id: 0=mpeg-2, 1=mpeg-1
        // layer 00=undefined, 01=layer 3, 10=layer2, 11=layer1

        // first off, 00 is always a bogus layer
        if (layer == 0)
            return -1;
        if (id == 0) {
            // mpeg-2
            if (layer==1)
                return MPEG2_LAYER3;
            else if (layer==2)
                return MPEG2_LAYER2;
            else if (layer==3)
                return MPEG2_LAYER1;
            else
                return -1;
        } else if (id == 1) {
            // mpeg-1
            if (layer==1)
                return MPEG1_LAYER3;
            else if (layer==2)
                return MPEG1_LAYER2;
            else if (layer==3)
                return MPEG1_LAYER1;
            else
                return -1;
        } 
        // fall through (impossible?)
        return -1;
    }


    public int getFrameRateFromByte3 (int byte3, boolean isMPEG2) {
        int freqBits = (byte3 & 0x0c) >>> 2;
        System.out.println ("freqBits are " +
                            lowNybbleToBinary (freqBits));
        if (isMPEG2) {
            if (freqBits == 0)
                return 22050;
            else if (freqBits == 1)
                return 24000;
            else if (freqBits == 2)
                return 16000;
            else
                return -1;
        } else {
            if (freqBits == 0)
                return 44100;
            else if (freqBits == 1)
                return 48000;
            else if (freqBits == 2)
                return 32000;
            else
                return -1;
        }
    }


    public String lowNybbleToBinary (int someByte) {
        StringBuffer sb = new StringBuffer (4);
        for (int i=3; i>=0; i--) {
            sb.append (Integer.toString( ((someByte >>> i) & 0x0001)));
        }
        return sb.toString();
    }


    public static void main (String[] args) {
        if (args.length != 1) {
            System.out.println ("Usage: ScanningInputStream <url>");
            return;
        }
        try {
            URL url = new URL (args[0]);
            BufferedInputStream bstream = 
                new BufferedInputStream (url.openStream());
            ScanningInputStream sstream =
                new ScanningInputStream (bstream);
            sstream.cueToFrameSync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
