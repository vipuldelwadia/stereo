package player;

import java.io.InputStream;

import javax.media.Time;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.InputSourceStream;
import javax.media.protocol.PullDataSource;
import javax.media.protocol.PullSourceStream;

public class OldInputStreamDataSource extends PullDataSource {
    
    private InputStream       input;
    private ContentDescriptor contentDescriptor;
    private boolean           isStopped;
    private int               stopCount;
    
    public OldInputStreamDataSource(InputStream _input) {
        this.input = _input;
        this.contentDescriptor = new ContentDescriptor(ContentDescriptor.CONTENT_UNKNOWN);
    }
    
    public OldInputStreamDataSource(InputStream _input, ContentDescriptor _desc) {
        this.input = _input;
        this.contentDescriptor = _desc;
    }
    
    @Override
    public PullSourceStream[] getStreams() {
        PullSourceStream[] streams = new PullSourceStream[1];
        streams[0] = new InputSourceStream(this.input, this.contentDescriptor);
        return streams;
    }
    
    @Override
    public void connect() {
        // TODO
    }
    
    @Override
    public void disconnect() {
        System.out.println("disconnect");
    }
    
    @Override
    public String getContentType() {
        return null;
    }
    
    @Override
    public Object getControl(String arg0) {
        return null;
    }
    
    @Override
    public Object[] getControls() {
        return null;
    }
    
    @Override
    public Time getDuration() {
        // TODO
        // return Time.TIME_UNKNOWN;
        return new Time(0);
    }
    
    @Override
    public void start() {
    }
    
    @Override
    public synchronized void stop() {
        if (!this.isStopped) {
            this.stopCount++;
            System.out.println("Incremented count");
            if (this.stopCount == 3) {
                System.out.println("Hello");
                this.isStopped = true;
                this.notifyAll();
                this.stopCount = 0;
            }
        }
    }
    
    public boolean isFinished() {
        return this.isStopped;
    }
}
