package player;

import java.io.IOException;
import java.io.InputStream;

import javax.media.CannotRealizeException;
import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;

public class OldInputStreamPlayer {
    
    private static final boolean           DEBUG = true;
    
    private static OldInputStreamPlayer       instance;
    
    private volatile OldInputStreamDataSource dataSource;
    private Player                         player;
    private boolean                        isPlaying;
    
    private OldInputStreamPlayer() {
        this.dataSource = null;
        this.player = null;
        this.isPlaying = false;
    }
    
    public boolean setInputStream(InputStream _input) {
        if (this.player != null)
            this.stop();
        if (isValidInputStream(_input)) {
            this.dataSource = constructInputStreamDataSource(_input);
            return true;
        }
        this.dataSource = null;
        return false;
    }
    
    public void start() throws NoPlayerException, IOException, CannotRealizeException {
        if (this.dataSource != null && !this.isPlaying) {
            if (this.player == null)
                this.player = Manager.createRealizedPlayer(this.dataSource);
            this.player.start();
            this.isPlaying = true;
        }
        else if (DEBUG) {
            if (this.dataSource == null)
                System.err.println("Datasource doesn't exist.");
            else if (this.isPlaying)
                System.err.println("Inputstream is already playing.");
        }
    }
    
    public void pause() {
        if (this.player != null && this.isPlaying) {
            this.player.stop();
            this.isPlaying = false;
        }
        
        else if (DEBUG) {
            if (this.player == null)
                System.err.println("Player doesn't exist.");
            else if (!this.isPlaying)
                System.err.println("Inputstream isn't playing.");
        }
    }
    
    public void stop() {
        if (this.player != null) {
            this.player.stop();
            this.player.close();
            this.player = null;
            this.isPlaying = false;
        }
        else if (DEBUG) {
            if (this.player == null)
                System.err.println("Player doesn't exist.");
        }
    }
    
    public boolean isPlaying(){
        
        return this.isPlaying;
    }
    
    public void waitForFinish() throws InterruptedException {
        synchronized (this.dataSource) {
            this.dataSource.wait();
        }
    }
    
    public boolean isFinished() {
        return this.dataSource.isFinished();
    }
    
    private boolean isValidInputStream(InputStream _input) {
        // TODO
        return _input != null;
    }
    
    private OldInputStreamDataSource constructInputStreamDataSource(InputStream _input) {
        return new OldInputStreamDataSource(_input);
    }
    
    public static OldInputStreamPlayer getInstance() {
        if (instance == null)
            instance = new OldInputStreamPlayer();
        return instance;
    }
}
