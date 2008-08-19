package net.sourceforge.jicyshout.jicylib1;

/*
  jicyshout relased under terms of the lesser GNU public license 
  http://www.gnu.org/licenses/licenses.html#TOCLGPL
 */

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.StringTokenizer;
import net.sourceforge.jicyshout.jicylib1.metadata.*;

/**

    @author Chris Adamson, invalidname@mac.com
 */
public class ID3InputStream extends BufferedInputStream 
    implements MP3MetadataParser {

    // would this be better as a pushback stream?


    MP3TagParseSupport tagParseSupport;

    /** Tags that have been discovered in the stream.
     */
    HashMap tags;

    /** for read(), which calls <code>read(byte[], int, int)</code>
     */
    protected byte[] bittyBuffer = new byte[1];

    public static int MAX_SIZE = 40 * 1024; // 40K max for one ID3 tag (block)

    protected byte[] id3ScanBuf = new byte [MAX_SIZE];

    // constructor
    public ID3InputStream (InputStream in) 
        throws IOException {
        super(in, MAX_SIZE);
        tags = new HashMap();
        tagParseSupport = new MP3TagParseSupport();
    }

    // to consider -- a constructor with a boolean to strip
    // the tags (ie, to not reset when we're done reading one)

    /** trivial call to <code>read (byte[1], 0, 1)</code> with some
        -1 handling, allows that other <code>read</code> to do the
        ID3 parsing.
     */
    public int read() throws IOException {
        int bytesRead = read (bittyBuffer, 0, 1);
        if (bytesRead == -1)
            return -1;
        else
            return (int) bittyBuffer[0];
    }

    // everything goes through here, including read()

    /** this read looks for ID3 tags in what it reads.  the other
        read methods go through here.
     */
    public int read (byte[] buf, int offset, int length)
        throws IOException {
        int bytesRead = super.read (buf, offset, length);

        // scan for ID3 headers
        for (int i=offset; i<offset+length-3; i++) {
            if ( (((char) buf[i]) == 'I') &&
                 (((char) buf[i+1]) == 'D') &&
                 (((char) buf[i+2]) == '3')) {
                // probable id3 header... scan it
                int viableBytes = bytesRead - i;
                scanProbableID3Header (buf, i, viableBytes);
            }
        }
        return bytesRead;
    }

    /** Scans a likely ID3 header that starts at offset.  Do not
        call unless the first three bytes here are "ID3".
        @param buf buffer that was read into
        @param int index of the 'I' in the 'ID3' string
        @param int viableBytes how many bytes including buf[offset] are
        real data (eg, bytesRead-offset)
     */
    private void scanProbableID3Header (byte[] buf,
                                        int offset,
                                        int viableBytes)
        throws IOException {
        System.arraycopy (buf, offset, id3ScanBuf, 0, 3);
        // we need the next 7 bytes to complete the header

        // TODO: check that this doesn't go out of bounds,
        // mark and read if necessary... oh that's gonna suck
        // (refactor - consider pushback if ID3 is in last
        // 10 bytes of a read)
        System.arraycopy (buf, offset+3, id3ScanBuf, 3, 7);
        System.out.println ("probable ID3 header...");
        // get the tag length from the last four header bytes
        // (note: this is "synchsafe", meaning the top bit
        // is always 0)
        int size = 0;
        for (int i=6; i<=9; i++) {
            size = size << 7;
            size = size | id3ScanBuf[i];
        }
        System.out.println ("ID3 tag size is " + size +
                            " (does not include header)");

        // copy over that many bytes.  may have to read more
        // (note: size does not include ID3 header or footer)
        if (viableBytes >= size) {
            System.out.println ("already have all tag bytes in memory");
            System.arraycopy (buf, offset+10, id3ScanBuf, 10, size);
            parseScanBuf(size);
        } else {
            System.out.println ("crap, we're in trouble, size is " +
                                size + ", viable is " + viableBytes +
                                " buffer.length is " + buf.length);
            
            // copy what we have
            System.arraycopy (buf, offset+10, id3ScanBuf, 10, viableBytes-10);
            // then get more 
            int needed = size - viableBytes + 10;
            System.out.println ("need to read " + needed +
                                " more bytes to get tag in memory");
            byte[] parseBuf = new byte [needed];
            mark(needed);
            int extraBytesRead =
                read (parseBuf, 0, needed);
            // TODO: make sure extraBytesRead == needed
            System.out.println ("read extra " + extraBytesRead);
            System.out.println ("copying " + needed + " to indexes " +
                                viableBytes + " thru " +
                                (viableBytes + needed));
            System.arraycopy (parseBuf, 0, id3ScanBuf, viableBytes, needed);
            // now we're set
            parseScanBuf(size);
            // now unread the read-ahead
            reset();
        }
    }


    /** trivial <code>return read (buf, 0, buf.length)</code>
     */
    public int read (byte[] buf)
        throws IOException {
        return read (buf, 0, buf.length);
    }


    /** brancher for ID3v2.0 (3-byte names) and ID3v.2.3 
        (4-byte names)
        @param size the tag size (implicit in the last four header
        bytes, but why bother recomputing?)
     */
    private void parseScanBuf(int size) {
        int id3subversion = id3ScanBuf [3];
        System.out.println ("ID3v2." + id3subversion);
        if (id3subversion <= 2)
            parseScanBufID320 (size);
        else 
            parseScanBufID323 (size);
        // todo - explicitly look for 2.3 and 2.4 and bail if
        // it's some newer version?
    }
    

    /** gets data from id3ScanBuf, which was populated in
        scanPossibleID3Header.  Assumes the entire tag is in
        the id3ScanBuf array.
        @param size the tag size (implicit in the last four header
        bytes, but why bother recomputing?)
    */
    private void parseScanBufID320 (int size) {
        int index = 10;
        while (index < size) {
            // are we in the padding?
            if ((id3ScanBuf[index] == 0x00) &&
                (id3ScanBuf[index+1] == 0x00) &&
                (id3ScanBuf[index+2] == 0x00)) {
                System.out.println ("found padding");
                break;
            }
                
            // scan 3-character header names and their "synchsafe" sizes
            String frameName = new String (id3ScanBuf, index, 3);
            index += 3;
            System.out.println ("name: " + frameName);
            // three bytes of size
            int frameSize = 0;
                // for (int i=6; i<=9; i++) {
                for (int i=index; i<index+3; i++) {
                    frameSize = frameSize << 7;
                    frameSize = frameSize | id3ScanBuf[i];
                }
            System.out.println ("size: " + frameSize);
            index += 3;
            if (isID320StringName (frameName)) {
                // encoding
                // eh, skip for now
                // index += 1;  // no, this seems to count in frameSize
                // just string values for now
                String value = new String (id3ScanBuf, index, frameSize);
                System.out.println ("value: " + value);
                System.out.println ("---");
            // hey, we got ourselves a tag!
            addTag (new ID3StringTag (frameName, value));
            }
            index += frameSize;
        } // while
        System.out.println ("out of while");
    }

    /** true if the 3-char frame name is known to denote
        string data in ID3v2.0 (which means we have to
        account for an encoding-type byte, also means it's
        really easy to get the value)
     */
    private boolean isID320StringName (String frameName) {
        // all "T" tags are strings, except TXX
        if ((frameName.charAt(0) == 'T') &&
            (! frameName.equals ("TXX")))
            return true;
        // so are all "W"s (URL's) except WXX
        if ((frameName.charAt(0) == 'W') &&
            (! frameName.equals ("WXX")))
            return true;
        // fall through (any others we should catch?)
        return false;
    }



    /** gets data from id3ScanBuf, which was populated in
        scanPossibleID3Header.  Assumes the entire tag is in
        the id3ScanBuf array.
        @param size the tag size (implicit in the last four header
        bytes, but why bother recomputing?)
    */
    private void parseScanBufID323 (int size) {
        int index = 10;
        while (index < size) {
            // are we in the padding?
            if ((id3ScanBuf[index] == 0x00) &&
                (id3ScanBuf[index+1] == 0x00) &&
                (id3ScanBuf[index+2] == 0x00)) {
                System.out.println ("found padding");
                break;
            }
                

            // scan 4-character header names and their "synchsafe" sizes
            String frameName = new String (id3ScanBuf, index, 4);
            index += 4;
            System.out.println ("name: " + frameName);
            // four bytes of size
            int frameSize = 0;
                for (int i=index; i<index+4; i++) {
                    frameSize = frameSize << 7;
                    frameSize = frameSize | id3ScanBuf[i];
                }
            System.out.println ("size: " + frameSize);
            index += 4;
            // flags
            // eh, skip 'em for now
            index += 2;
            if (isID323StringName(frameName)) {
                // text encoding (only present for string tags)
                // not doing anything with it for now
                // index += 1; // counted as part of size
                // just string values for now
                String value = new String (id3ScanBuf, index, frameSize);
                System.out.println ("value: " + value);
                System.out.println ("---");
                // hey, we got ourselves a tag!
                addTag (new ID3StringTag (frameName, value));
            }
            index += frameSize;
        } // while
        System.out.println ("out of while");
    }


    /** true if the 4-char frame name is known to denote
        string data in ID3v2.3 (which means we have to
        account for an encoding-type byte, and that the
        value is really easy to get)
     */
    private boolean isID323StringName (String frameName) {
        // all "T" tags are strings, except TXXX
        if ((frameName.charAt(0) == 'T') &&
            (! frameName.equals ("TXXX")))
            return true;
        // so are all "W"s (URL's)
        if (frameName.charAt(0) == 'W')
            return true;
        // todo - compile list of other known string-indicators
        // like COMM, USER, etc.
        return false;
    }






    /** adds the tag to the HashMap of tags we have encountered
        either in-stream or as headers, replacing any previous
        tag with this name.
     */
    protected void addTag(ID3Tag tag) {
        tags.put (tag.getName(), tag);
        // fire this as an event too
        tagParseSupport.fireTagParsed (this, tag);
    }

    /** Get the named tag from the HashMap of headers and
        in-line tags.  Null if no such tag has been encountered.
     */
    public MP3Tag getTag (String tagName) {
        return (MP3Tag) tags.get (tagName);
    }

    /** Get all tags (headers or in-stream) encountered thus far.
     */
    public MP3Tag[] getTags() {
        return (MP3Tag[]) tags.values().toArray (new MP3Tag[0]);
    }

    /** Returns a HashMap of all headers and in-stream tags
        parsed so far.
     */
    public HashMap getTagHash() {
        return tags;
    }

    /** Adds a TagParseListener to be notified when this stream
        parses MP3Tags.
     */
    public void addTagParseListener (TagParseListener tpl) {
        tagParseSupport.addTagParseListener (tpl);
    }

    /** Removes a TagParseListener, so it won't be notified when
        this stream parses MP3Tags.
     */
    public void removeTagParseListener (TagParseListener tpl) {
        tagParseSupport.removeTagParseListener (tpl);
    }
    
    /** Quickie unit-test.
     */
    public static void main (String args[]) {
        byte[] chow = new byte[30];
        if (args.length != 1) {
            System.out.println ("Usage: ID3InputStream <url>");
            return;
        }
        try {
            URL url = new URL (args[0]);
            URLConnection conn = url.openConnection();
            // conn.setRequestProperty ("Icy-Metadata", "1");
            ID3InputStream id3In =
                new ID3InputStream(new BufferedInputStream(conn.getInputStream()));
            while (id3In.available() > -1) {
                id3In.read (chow, 0, chow.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



} // ID3InputStream
