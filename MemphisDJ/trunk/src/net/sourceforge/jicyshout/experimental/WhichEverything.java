package net.sourceforge.jicyshout.experimental;

/*
  jicyshout relased under terms of the lesser GNU public license 
  http://www.gnu.org/licenses/licenses.html#TOCLGPL
 */

import javax.media.*;
import javax.media.protocol.*;
import java.net.URL;

/** figures out what MediaLocator, DataSource, Player we get
    from Manager and other JMF defaults.
@author Chris Adamson, invalidname@mac.com
 */
public class WhichEverything extends Object {

    /* results from this:
      file:///Users/cadamson/Breakaway.mp3
Got MediaLocator: file:/Users/cadamson/Breakaway.mp3
Got DataSource: com.sun.media.protocol.file.DataSource@7acafa, its content type is audio.mpeg
Got Player: com.sun.media.content.unknown.Handler@19ed8

      http://www.hollycole.com/Multimedia/Mpeg/callingyou96.mp3
Got MediaLocator: http://www.hollycole.com/Multimedia/Mpeg/callingyou96.mp3
Got DataSource: com.sun.media.protocol.http.DataSource@1fadf0, its content type is audio.mpeg
Got Player: com.sun.media.content.unknown.Handler@64369d

     */

    public static void main (String[] args) {
        if (args.length != 1) {
            System.out.println ("Usage: java WhichEverything <file-url>");
            return;
        }
        try {
            URL url = new URL (args[0]);
            MediaLocator ml = new MediaLocator (url);
            System.out.println ("Got MediaLocator: " + ml);
            DataSource ds = Manager.createDataSource (ml);
            System.out.println ("Got DataSource: " + ds +
                                ", its content type is " +
                                ds.getContentType());
            Player p = Manager.createRealizedPlayer (ds);
            System.out.println ("Got Player: " + p);
            p.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // main

}
