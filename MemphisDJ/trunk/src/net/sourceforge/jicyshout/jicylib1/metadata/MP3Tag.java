package net.sourceforge.jicyshout.jicylib1.metadata;

/*
  jicyshout relased under terms of the lesser GNU public license 
  http://www.gnu.org/licenses/licenses.html#TOCLGPL
 */

/** An individual piece of mp3 metadata, as a name/value pair.
    Abstract just so that subclasses will indicate their
    tagging scheme (Icy, ID3, etc.).
 */
public abstract class MP3Tag extends Object {

    protected String name;
    protected Object value;

    public MP3Tag (String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public String toString() {
        return getClass().getName() + " -- " +
            getName() + ":" + getValue().toString();
    }

}
