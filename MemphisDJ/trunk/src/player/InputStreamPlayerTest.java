package player;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.media.NoPlayerException;

import org.junit.Before;
import org.junit.Test;

public class InputStreamPlayerTest {
    
    public OldInputStreamPlayer player;
    
//    @Test
//    public void testStartAndStopListening() {
//        try {
//            // Initial Start & Stop
//            boolean isListening = this.player.startListening();
//            if (!isListening)
//                fail("Failed to listen to stream");
//            if (this.player.isListening() != isListening)
//                fail("Player has incorrect listening boolean on start");
//            
//            isListening = this.player.stopListening();
//            if (isListening)
//                fail("Failed to stop listening to stream");
//            if (this.player.isListening() != isListening)
//                fail("Player has incorrect listening boolean on stop");
//                        
//            // Double stop
//            isListening = this.player.stopListening();
//            if (isListening)
//                fail("Failed to stop listening to stream");
//            if (this.player.isListening() != isListening)
//                fail("Player has incorrect listening boolean on stop");
//            
//            // Double start
//            isListening = this.player.startListening();
//            if (!isListening)
//                fail("Failed to listen to stream");
//            if (this.player.isListening() != isListening)
//                fail("Player has incorrect listening boolean on start");
//            
//            isListening = this.player.startListening();
//            if (!isListening)
//                fail("Failed to listen to stream");
//            if (this.player.isListening() != isListening)
//                fail("Player has incorrect listening boolean on start");
//            
//        }
//        catch (NoPlayerException e) {
//            fail("No player found!");
//            e.printStackTrace();
//        }
//        catch (IOException e) {
//            fail("File not found!");
//            e.printStackTrace();
//        }
//    }
    
//    @Before
//    public void setUp() throws Exception {
//        InputStream stream = new FileInputStream(new File("test/rappin.wav"));
//        this.player = new OldInputStreamPlayer(stream);
//    }
}
