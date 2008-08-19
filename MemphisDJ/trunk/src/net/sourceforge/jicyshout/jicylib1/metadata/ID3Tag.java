package net.sourceforge.jicyshout.jicylib1.metadata;

/*
  jicyshout relased under terms of the lesser GNU public license 
  http://www.gnu.org/licenses/licenses.html#TOCLGPL
 */

/** A tag parsed from an ID3 tag.  See http://www.id3.org/ for more.
 */
public class ID3Tag extends MP3Tag {

    public ID3Tag (String name,
                   Object value) {
        super (name, value);
    }

}
