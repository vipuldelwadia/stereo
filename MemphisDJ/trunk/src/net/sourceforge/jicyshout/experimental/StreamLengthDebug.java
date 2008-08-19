package net.sourceforge.jicyshout.experimental;

/*
  jicyshout relased under terms of the lesser GNU public license 
  http://www.gnu.org/licenses/licenses.html#TOCLGPL
 */


import java.net.URL;
import java.net.URLConnection;

/** simple test - looks at http content-length.
    @author Chris Adamson, invalidname@mac.com
 */
public class StreamLengthDebug extends Object {
    /* looking at the possibility that the problem with http
       streams comes from how com/sun/media/protocol/DataSource's
       connect() uses URLConnection to get content-lengths

      results of this:
      file:/Users/cadamson/Breakaway.mp3 content length == 5938940
      http://www.hollycole.com/Multimedia/Mpeg/callingyou96.mp3 content length == 995418
      http://65.165.174.100:8000/som content length == -1
    */

    public static void main (String[] args){
        if (args.length != 1) {
            System.out.println ("Usage: java StreamLengthDebug <stream-url>");
            return;
        }
        try {
            URL url = new URL (args[0]);
            URLConnection conn = url.openConnection();
            System.out.println (url + " content length == " +
                                conn.getContentLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
