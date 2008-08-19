package net.sourceforge.jicyshout.experimental;

/*
  jicyshout relased under terms of the lesser GNU public license 
  http://www.gnu.org/licenses/licenses.html#TOCLGPL
 */

import java.net.*;
import java.io.*;

/** Bounces an http request with "Icy-Metadata" and
    "x-audiocast-udpport" off a public SnoopServlet to show
    the headers as a server sees them.
    @author Chris Adamson, invalidname@mac.com
 */
public class TestIcyRequest extends Object {

    public static void main (String[] args) {
        try {
            URL url =
                new URL ("http://www.interec.net/servlet/SnoopServlet");
            URLConnection conn = url.openConnection();
            conn.setRequestProperty ("Icy-Metadata", "1");
            conn.setRequestProperty ("x-audiocast-udpport", "10000");
            BufferedReader buffy =
                new BufferedReader (new InputStreamReader (conn.getInputStream()));
            while (buffy.ready()) {
                System.out.println (buffy.readLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
