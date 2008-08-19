package net.sourceforge.jicyshout.experimental;

/*
  jicyshout relased under terms of the lesser GNU public license 
  http://www.gnu.org/licenses/licenses.html#TOCLGPL
 */


/** Simple debug stuff, useful for reading mpeg and id3 headers.
    @author Chris Adamson, invalidname@mac.com
 */
public class TestByteToHex extends Object {

    public static char[] HEX = {
        '0', '1', '2', '3', '4', '5', '6', '7', 
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    public static String byteToHex (byte b) {
        // dump binary to stdout for sanity check
        for (int i=7; i>=0; i--) {
            System.out.print ((b >>> i) & 0x0001);
        }
        System.out.println("");
        // cast up to int to move sign out where we won't see it
        int bottomNybble = b & 0x000f;
        int topNybble = (b & 0x00f0) >>> 4;
        char[] chars = new char[2];
        chars[0] = HEX [topNybble];
        chars[1] = HEX [bottomNybble];
        return new String (chars);
    }

    public static void main (String[] args) {
        if (args.length != 1) {
            System.out.println ("Usage: TestByteToHex <dec>");
            return;
        }
        byte b = Byte.parseByte (args[0]);
        System.out.println (byteToHex(b));
    }

}
