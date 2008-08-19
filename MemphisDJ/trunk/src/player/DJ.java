package player;

import java.io.IOException;
import java.io.InputStream;

import javax.media.CannotRealizeException;
import javax.media.NoPlayerException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.jl.decoder.JavaLayerException;
import music.Track;
import daap.DaapClient;

public class DJ {
    
    /**
     * @param args
     * @throws IOException
     * @throws NoPlayerException
     * @throws InterruptedException 
     * @throws CannotRealizeException 
     * @throws UnsupportedAudioFileException 
     * @throws JavaLayerException 
     */
    public static void main(String[] args) throws IOException, NoPlayerException, InterruptedException, CannotRealizeException, UnsupportedAudioFileException, JavaLayerException {
        DaapClient client = new DaapClient("majoribanks.mcs.vuw.ac.nz", 3689);
        
//        for (Track t : client.getTrackList()) {
            
        Track t = client.getTrackList().get(2);
        
            System.out.println(t.getTrackId());
            InputStream stream = client.getStream(t);            //InputStreamPlayer player = InputStreamPlayer.getInstance();
            //player.setInputStream(stream);
            //player.start();
            
            InputStreamPlayer player = new InputStreamPlayer(stream);
            player.start();
            
            
            Thread.sleep(10000);
            
            player.pause();
            
            Thread.sleep(1000);
            
            synchronized (player) {
//                player.notifyAll();
//                player.unpause();
            }
            
            
            //AudioPlayer player = new AudioPlayer();
            //player.play(AudioSystem.getAudioInputStream(new BufferedInputStream(stream)));
            
            /*
            Thread.sleep(500);
            
            player.pause();
            Thread.sleep(200);
            player.start();
            
            
            Thread.sleep(2000);*/
            
//            while(!player.isFinished()){
//                player.waitForFinish();
//            }
                
            // System.out.println("Stopped Playing");
            // player.stop();
            
            // System.out.println("Is Listening: "+player.stopListening());
//        }
        // }
    }
}
