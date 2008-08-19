package net.sourceforge.jicyshout.jicylib1;

/*
  jicyshout relased under terms of the lesser GNU public license 
  http://www.gnu.org/licenses/licenses.html#TOCLGPL
 */

import javax.media.*;
import java.io.*;


/** Given a fresh (unread) BufferedInputStream, gets an MP3-aware
    (ie, tag-parsing) stream.
    @author Chris Adamson, invalidname@mac.com
 */
public class MP3InputStreamFactory extends Object {

    private static MP3InputStreamFactory instance;

    public static final String ICY_MAGIC_STRING = "ICY 200 OK";

    /** secret constructor... go away kid
     */
    private MP3InputStreamFactory() { super(); }

    public static MP3InputStreamFactory getInstance() {
        if (instance == null)
            instance = new MP3InputStreamFactory();
        return instance;
    }

    /** looks at the metadata in the stream (if any is found)
        and produces an appropriate stream to parse and/or
        strip the metadata.
        @param in InputStream to parse
        @param maxBytes maximum bytes to parse
        @return an InputStream to parse and possibly strip any
        metadata in the stream.  If nothing interesting is 
        found in the stream, this may return the original InputStream
        itself.
     */
    protected InputStream getMP3InputStreamForImpl (InputStream in,
                                                    int maxBytes) {
        /* strategy - here we create a PushbackInputStream and
           look for tell-tale signs of the various tagging schemes.
           When one fails, we push its bytes back in and try
           another.

           We want to _not_ consume any bytes from the stream
           before the constructed stream has a chance to read them.
           Using a pushback stream accomplishes this, and may be
           better than a buffered stream for this, since after the 
           parsing and the first few reads, the pushback will always
           be empty and thus easily (cheaply) ignored.

           One thing that sucks about this is that since we send
           pushy to the new stream's constructors, it sticks around,
           even after they've drained its pushback buffer and are
           just getting fresh bytes from the underlying stream.  It
           would be nice at some point to be able to re-route the
           stream once the pushback buffer is empty, since we won't
           use it again.  So, in an icy case, we'd eventually like
           to change
           [HttpInputStream] --> [PushbackInputStream] --> [IcyInputStream]
           to
           [HttpInputStream] --> [IcyInputStream]
           thus allowing the PushbackInputStream to gc and release
           its useless 32K buffer (yes, I'm old enough to think that
           wasting 32k is bad... that's twice as much as my first
           computer had)
         */
        InputStream returnedStream = in; // default - no wrapper
        try {
            PushbackInputStream pushy =
                new PushbackInputStream (in, maxBytes);

            // TODO: take a peek for an MPEG header and remember
            // its vital statistics (encoding type, freq, bitrate, etc.)

            // first, look to see if this is icy (shoutcast)
            // (note - isShoutcastStream must push back everything!)
            if (isShoutcastStream (pushy, maxBytes)) {
                System.out.println ("*** stream is Icy");
                returnedStream = new IcyInputStream (pushy);
            }
            // ok, is it ID3?
            else if (isID3Stream (pushy, maxBytes)) {
                System.out.println ("*** stream is ID3");
                returnedStream = new ID3InputStream (pushy);
            }
            // other scans would go here
            else {
                System.out.println ("*** stream is nothing special");
            }
        } catch (IOException ioe) {
            // barf -- we'll just get returnedStream
        }
        return returnedStream;
    }

    /** true if ICY_MAGIC_STRING appears in the first 15 bytes
        of the stream, and yes, "icy" means "shoutcast", 
        not "icecast".
     */
    protected boolean isShoutcastStream (PushbackInputStream pushy,
                                       int maxBytes)
        throws IOException {
        // read bytes and immediately push them back
        byte[] buf = new byte[15];
        int bytesRead = pushy.read (buf, 0, buf.length);
        pushy.unread (buf, 0, bytesRead);
        // now, did we get ICY_MAGIC_STRING?
        String bufString = new String (buf, 0, bytesRead);
        // System.out.println ("top of stream is " + bufString);
        return (bufString.indexOf(ICY_MAGIC_STRING) != -1);
    }



    /** Determines whether the stream contains ID3 tags.
        For our purposes, we return true if we find a run
        of 10 bytes such that:
        <ol>
        <li>The first three bytes are "ID3"
        <li>Neither of the next two bytes are 0xFF
        <li>The sixth byte is a plausible "flags" byte
        <li>Bytes seven through ten are less than 0x80
        </ol>
        @see <a href="http://www.id3.0rg/id3v2.4.0-structure.txt">ID3
        tag version 2.4.0 - Main Structure</a>
     */
    protected boolean isID3Stream (PushbackInputStream pushy,
                                   int maxBytes)
        throws IOException {
        // scheme -- read in 10 bytes, scan, if we fail then
        // read in another byte and move to next index
        byte[] scanBuf = new byte [maxBytes];
        int index = 0;
        int bailIndex = maxBytes - 10; // last index w/10 bytes to read
        boolean found = false;
        pushy.read (scanBuf, 0, 10);
        while ((!found) &&
               (index < bailIndex)) {
            // first test - first 3 bytes are "ID3"
            if (((char) scanBuf[index] != 'I') ||
                ((char) scanBuf[index+1] != 'D') ||
                ((char) scanBuf[index+2] != '3')) {
                // read next byte in and bail
                pushy.read (scanBuf, index+10, 1);
                index++;
                continue;
            }
            // second test -- either of the next two bytes are 0xFF
            if (((char) scanBuf[index+3] == 0xff) ||
                ((char) scanBuf[index+4] == 0xff)) {
                // read next byte in and bail
                pushy.read (scanBuf, index+10, 1);
                index++;
                continue;
            }
            // third test -- sixth byte is a plausible "flags" byte
            // todo: something with scanBuf[index+5]

            // fourth test - bytes seven through ten are less than 0x80
            // (ie, high bit always off because of unsynchronization)
            if ( (((scanBuf[index+6] >>> 7) & 0x01) == 1) ||
                 (((scanBuf[index+7] >>> 7) & 0x01) == 1) ||
                 (((scanBuf[index+8] >>> 7) & 0x01) == 1) ||
                 (((scanBuf[index+9] >>> 7) & 0x01) == 1)) {
                // read next byte in and bail
                pushy.read (scanBuf, index+10, 1);
                index++;
                continue;
            }
            // if we made it here, we assume this really is ID3
            // TODO: double check the math on unread!
            pushy.unread (scanBuf, 0, index+10);
            return true;
        } // while
        // loser!
        // TODO: double check the math on unread!
        pushy.unread (scanBuf, 0, index+10); // should be entire buffer
        return false;
    }




    /** Get an mp3-handling stream appropriate to the
        metadata content (if any) found in the stream, ie,
        one that parses and if necessary strips the metadata
        from the stream.  The returned stream <i>may</i> implement
        MP3InputStreamParser.
     */
    public static InputStream getMP3InputStreamFor (InputStream in,
                                                    int maxBytes) {
        return getInstance().getMP3InputStreamForImpl (in, maxBytes);
    }

}
