package net.sourceforge.jicyshout.jicylib1;

/*
  jicyshout relased under terms of the lesser GNU public license 
  http://www.gnu.org/licenses/licenses.html#TOCLGPL
 */

import javax.swing.JFrame;
import javax.media.*;
import javax.media.protocol.*;
import java.net.*;
import java.io.*;
import java.util.HashMap; // java 1.2 or collections
import net.sourceforge.jicyshout.jicylib1.metadata.*;

/** A DataSource that uses a SeekableStream suitable for streaming
    MP3 audio with the default parser supplied by JMF.  Also 
    provides metadata support for the Ice (and soon ID3) formats.
    <p>
    See SeekableInputStream for ugly details of how we get the
    JMF MP3 parser to not blow up.
    <p>
    As an MP3MetadataParser, this DataSource allows listeners to
    register to get events when metadata is received.  This is
    preferable to adding listeners on the stream (as if it
    were visible!) because this allows the DataSource to aggregate
    several sources of metadata, such as the <code>udpport</code>
    scheme of receiving UDP metadata packets on another port.
    @author Chris Adamson, invalidname@mac.com
 */
public class SimpleMP3DataSource extends PullDataSource 
    implements TagParseListener, MP3MetadataParser {

    public static final int DEFAULT_UDPPORT = 6000;
    protected MediaLocator myML;
    protected InputStream httpStream;
    protected boolean parseStreamMetadata;
    protected SeekableInputStream seekStream;
    protected URLConnection urlConnection;
    protected PullSourceStream[] sourceStreams;
    protected Object[] EMPTY_CONTROL_ARRAY = {};
    protected int preferredUDPPort = -1;
    protected UDPMetadataListener iceListener;
    protected MP3TagParseSupport tagParseSupport;
    /** Tags encountered thus far, keys are names, values
        are MP3Tags
     */
    protected HashMap tagMap;

    /** Pretty much trivial, gets URL from from MediaLocator.
        @param parseStreamMetadata if true, the we try to get
        metadata from the stream, by requesting it with the
        "Icy-Metadata" request header (so that if the server 
        is Icecast/Shoutcast, it sends us in-stream metadata).
        <i>Future: this will also send the udpport request for
        UDP metadata, and will listen for ID3 tags</i>
        @exception MalformedURLException if the URL in the
        MediaLocator is malformed
     */
    public SimpleMP3DataSource (MediaLocator ml,
                                boolean parseStreamMetadata)
        throws MalformedURLException {
        super ();
        myML = ml;
        this.parseStreamMetadata = parseStreamMetadata;
        URL url = ml.getURL();
        tagMap = new HashMap();
        tagParseSupport = new MP3TagParseSupport();
        preferredUDPPort = DEFAULT_UDPPORT;
    }

    /** Trivial, does not ask for stream-parsed metadata.
     */
    public SimpleMP3DataSource (MediaLocator ml)
        throws MalformedURLException {
        this (ml, false);
    }

    /** Change the udp port requested for out-of-band udp
        icecast metadata.  MUST be called before connect()
        to have any effect, otherwise you get the
        DEFAULT_UDPPORT.
     */
    public void setPreferredUDPPort (int port) {
        preferredUDPPort = port;
    }

    /** Gets an InputStream from the MediaLocator and does
        some scanning on it to determine what kind of 
        metadata it can parse from it.  Specified by DataSource.
     */
    public void connect()
        throws IOException {
        try {
            System.out.println ("top of connect()");
            URL url = myML.getURL();
            // get the connection, maybe request icy metadata
            urlConnection = url.openConnection();
            System.out.println ("opened connection " +
                                urlConnection.getClass().getName());
            // only send this if we intend to get metadata
            if (parseStreamMetadata) {
                urlConnection.setRequestProperty ("Icy-Metadata",
                                                  "1");
                urlConnection.setRequestProperty ("x-audiocast-udpport",
                                       Integer.toString(preferredUDPPort));
            }

            // add any interesting http response headers
            // to the tags.  don't bother if protocol is "file", since
            // that just reflects our original request headers
            // (Icecast's "x-audiocast-udpport" will be one of these)
            for (int i=0;
                 (! url.getProtocol().equals("file"));
                 i++) {
                String headerKey = urlConnection.getHeaderFieldKey(i);
                String headerValue = urlConnection.getHeaderField(i);
                if ((headerKey == null) && (headerValue == null))
                    break;
                // I used to only provide ice "audiocast" headers as
                // tags, but since a listener can easily decide to ignore
                // http headers (ie, if they're instanceof HTTPHeaderTag),
                // we send them all
                if ((headerKey != null) && (headerValue != null))
                    addTag (new HTTPHeaderTag (headerKey, headerValue));
                // kick off UDPMetadataListener if this header is
                // the x-audiocast-uddpport value (and if this is really
                // from an http server!)
                if ((headerKey != null) &&
                    ((headerKey.indexOf("x-audiocast-udpport") != -1)) &&
                    (urlConnection instanceof HttpURLConnection)) {
                    try {
                        String host = url.getHost();
                        int port = Integer.parseInt(headerValue);
                        System.out.println ("** " + 
                                            headerKey + ": " + port);
                        iceListener = new UDPMetadataListener (host, port);
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                } // if
            }
            

            // get a metadata-parsing stream, if type can be
            // determined from first 1KB
            httpStream = urlConnection.getInputStream();
            InputStream mp3Stream; 
            if (parseStreamMetadata)
               mp3Stream =  MP3InputStreamFactory.getInstance().getMP3InputStreamFor (httpStream, 1024);
            else
                mp3Stream = httpStream;
            // if the stream is a metadata parser, and if we
            // want to get in-stream metadata, get all
            // tags currently parsed and listen for more
            if (parseStreamMetadata &&
                (mp3Stream instanceof MP3MetadataParser)) {
                MP3MetadataParser parser =
                    (MP3MetadataParser) mp3Stream;
                MP3Tag[] streamTags = parser.getTags();
                for (int i=0; i<streamTags.length; i++) {
                    addTag (streamTags[i]);
                }
                parser.addTagParseListener (this);
            }
            // make the stream seekable,
            // so jmf mp3 parser can handle it
            seekStream = new SeekableInputStream (mp3Stream);
            System.out.println ("got input stream");
            sourceStreams = new PullSourceStream[1];
            sourceStreams[0] = seekStream;
            System.out.println ("connect done, stream is " +
                                sourceStreams[0]);
        } catch (MalformedURLException murle) {
            throw new IOException ("Malformed URL: " +
                                   murle.getMessage());
        }
    }

    // removed bogus cueToFrameSync for now;  it only checked
    // for FFF/FFE... stuff in experimental is better -- chris

    /** Closes up InputStream.  Specified by DataSource.
     */
    public void disconnect() {
        try {
            seekStream.close();
            // does mp3Stream need to be instance variable
            // so we can close it here?
            httpStream.close();  // need to do this?
            // turn off the UDPMetadataListener, if there is one
            if (iceListener != null) {
                iceListener.setActive(false); // should fall out of loop
                iceListener = null; // de-ref and please gc
            }
        } catch (IOException ioe) {
            // well my life just sucks now
            System.out.println ("Can't close stream.  Sigh");
            ioe.printStackTrace();
            // can't throw a checked exception here.  sorry.
        }
    }

    /** Returns "audio.mpeg".  Specified by DataSource.
     */
    public String getContentType() {
        return "audio.mpeg";
        // return "unknown";
    }

    /** Does nothing, since bytes are pulled on-demand from
        the PullSourceStream.  Specified by DataSource.
     */
    public void start() {
        // don't know that this would ever be useful for us.
        // maybe it's meant for DataSources that have their
        // own threads filling up buffers or something
    }

    /** Does nothing, since bytes are pulled on-demand from
        the PullSourceStream.  Specified by DataSource.
     */
    public void stop() {
    }

    // PullDataSource abstract stuff we must provide

    /** Returns a one-member array with the SeekableInputStream.
        Specified by PullDataSource
     */
    public PullSourceStream[] getStreams() {
        return sourceStreams;
    }

    // Duration abstract stuff we must provide

    /** Returns DURATION_UNBOUNDED, since it's a theoretically
        endless stream.  Specified by Duration.
     */
    public Time getDuration () {
        return DataSource.DURATION_UNBOUNDED;
    }


    // Controls abstract stuff we must provide
    
    /** Returns null since we don't support any Controls.
        Specified by Controls.
     */
    public Object getControl(String controlName) {
        return null;
    }
    
    /** Returns an empty array since we don't support
        any Controls. Specified by Controls.
     */
    public Object[] getControls() {
        return EMPTY_CONTROL_ARRAY;
    }

    // stuff to support outside TagParseListeners

    /** Calls this class' addTag when a tag is parsed by
        the stream.
     */
    public void tagParsed (TagParseEvent evt) {
        System.out.println (evt.getTag());
        addTag (evt.getTag());
    }

    /** adds the tag to the HashMap of tags we have encountered
        either in-stream or as headers, replacing any previous
        tag with this name.
     */
    protected void addTag(MP3Tag tag) {
        tagMap.put (tag.getName(), tag);
        // fire this as an event too
        tagParseSupport.fireTagParsed (this, tag);
    }

    /** Get the named tag from the HashMap of headers and
        in-line tags.  Null if no such tag has been encountered.
     */
    public MP3Tag getTag (String tagName) {
        return (MP3Tag) tagMap.get (tagName);
    }

    /** Get all tags encountered thus far.
     */
    public MP3Tag[] getTags() {
        return (MP3Tag[]) tagMap.values().toArray (new MP3Tag[0]);
    }

    /** Returns a HashMap of all MP3Tags parsed so far.
     */
    public HashMap getTagHash() {
        return tagMap;
    }

    /** Adds a TagParseListener to be notified when this DataSource
        receives MP3Tags, from parsing the stream or via other means.
     */
    public void addTagParseListener (TagParseListener tpl) {
        tagParseSupport.addTagParseListener (tpl);
    }

    /** Removes a TagParseListener.
     */
    public void removeTagParseListener (TagParseListener tpl) {
        tagParseSupport.removeTagParseListener (tpl);
    }
        


    public static void main (String[] args) {
        if (args.length != 1) {
            System.out.println ("Usage: SimpleMP3DataSource <http-stream-url>");
            return;
        }
        try {
            // kludge - make mac os x do its app-launch stuff sooner
            JFrame nonFrame = new JFrame ();
            // this is the stuff that matters
            MediaLocator ml = new MediaLocator (new URL (args[0]));
            System.out.println ("Got MediaLocator");
            SimpleMP3DataSource ds = new SimpleMP3DataSource (ml, false);
            // use the following version if you want metadata
            // SimpleMP3DataSource ds = new SimpleMP3DataSource (ml, true);
            System.out.println ("Got SimpleMP3DataSource");
            ds.connect();
            System.out.println ("Connected DataSource");
            Player p = Manager.createPlayer (ds);
            System.out.println ("Got Player");
            p.setSource (ds);
            System.out.println ("Set Player's DataSource");
            p.realize();
            System.out.println ("Called realize()");
            // wait until realized
            while (p.getState() != Controller.Realized) {
                Thread.sleep (100);
            }
            System.out.println ("Realized");
            p.start();
            System.out.println ("Started");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

}
