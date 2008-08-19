package net.sourceforge.jicyshout.experimental;

/*
  jicyshout relased under terms of the lesser GNU public license 
  http://www.gnu.org/licenses/licenses.html#TOCLGPL
 */

import javax.media.*;
import javax.media.bean.playerbean.*;
import java.net.URL;

/** figures out what class is loaded for an mp3 local file
    (or whatever args[0] turns out to be.  for
    file:///Users/cadamson/some-file.mp3, it's
    com.sun.media.content.unknown.Handler, 
    which is a meaningless subclass of com.sun.media.MediaHandler...
    <p>
    output is:
    got player com.sun.media.content.unknown.Handler, hash=8155116
    Class heirarchy:
    com.sun.media.content.unknown.Handler
    com.sun.media.MediaPlayer
    com.sun.media.BasicPlayer
    com.sun.media.BasicController
    java.lang.Object
    @author Chris Adamson, invalidname@mac.com
 */
public class WhichPlayer extends Object {

    public static void main (String[] args) {
        try {
            URL url = new URL (args[0]);
            MediaLocator ml = new MediaLocator (url);
            Player p = Manager.createRealizedPlayer (ml);
            System.out.println ("got player " + p.getClass().getName() +
                                ", hash=" + p.hashCode());
            if (p instanceof MediaPlayer) {
                MediaPlayer mp = (MediaPlayer) p;
                System.out.println ("a wrapper for " +
                                    mp.getClass().getName() +
                                    ", hash=" + mp.hashCode());
            } else {
                System.out.println ("Class heirarchy:");
                printSuperclasses (p.getClass());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printSuperclasses (Class c) {
        System.out.println (c.getName());
        if (c != Object.class)
            printSuperclasses (c.getSuperclass());
    }
}
