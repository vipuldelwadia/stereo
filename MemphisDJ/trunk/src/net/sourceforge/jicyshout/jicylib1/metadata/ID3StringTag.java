package net.sourceforge.jicyshout.jicylib1.metadata;

/*
  jicyshout relased under terms of the lesser GNU public license 
  http://www.gnu.org/licenses/licenses.html#TOCLGPL
 */

/** An ID3 tag known to have a string value (artist, title, etc.)
 */
public class ID3StringTag extends ID3Tag 
    implements StringableTag {

    public ID3StringTag (String name,
                         String stringValue) {
        super (name, stringValue);
    }

    public String getValueAsString() {
        return (String) getValue();
    }
}
