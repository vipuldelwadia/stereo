package net.sourceforge.jicyshout.experimental;

/*
  jicyshout relased under terms of the lesser GNU public license 
  http://www.gnu.org/licenses/licenses.html#TOCLGPL
 */

import java.net.*;

/** dumps http headers to console.  asks for "Icy-Metadata"
    @author Chris Adamson, invalidname@mac.com
 */
public class HeaderScanner extends Object {

    public static void main (String[] args) {
        if (args.length != 1) {
            System.out.println ("usage: java HeaderScanner <url>");
            return;
        }
        try {
            URL url = new URL (args[0]);
            URLConnection conn = url.openConnection();
            // stuff we'd do for icecast, shoutcast streams
            conn.setRequestProperty ("Icy-Metadata", "1");
            conn.setRequestProperty ("x-audiocast-udpport", "10000");
            // now dig stuff out
            if (conn instanceof HttpURLConnection) {
                HttpURLConnection hconn =
                    (HttpURLConnection) conn;
                System.out.println ("HttpURLConnection response code = "+
                                    hconn.getResponseCode() +
                                    ", message is " +
                                    hconn.getResponseMessage());
            }
            for (int i=0; ;i++) {
                String headerKey = conn.getHeaderFieldKey(i);
                String headerValue = conn.getHeaderField(i);
                if ((headerKey == null) && (headerValue == null))
                    break;
                System.out.println ("("+ i + ") "+
                                    headerKey + ": " + headerValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // main

}
