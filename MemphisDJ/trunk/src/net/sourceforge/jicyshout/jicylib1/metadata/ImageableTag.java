package net.sourceforge.jicyshout.jicylib1.metadata;

/*
  jicyshout relased under terms of the lesser GNU public license 
  http://www.gnu.org/licenses/licenses.html#TOCLGPL
 */

import java.awt.Image;

/** Indicates that the value of a tag is an image, 
    provides a getValueAsImage() method to get it as 
    a java.awt.Image.
 */
public interface ImageableTag {

    /** Return the value of this tag as a java.awt.Image.
     */
    public Image getValueAsImage();

}
