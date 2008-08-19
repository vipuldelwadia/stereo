package net.sourceforge.jicyshout.gui;

/*
  jicyshout relased under terms of the lesser GNU public license 
  http://www.gnu.org/licenses/licenses.html#TOCLGPL
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.*;
import java.net.*;
import java.io.*;
import javax.media.*;
import javax.media.protocol.*;
import net.sourceforge.jicyshout.jicylib1.*;
import net.sourceforge.jicyshout.jicylib1.metadata.*;

/** a very simple proof-of-concept gui for jicyshout.
    @author Chris Adamson, invalidname@mac.com
 */
public class SimpleDevGUI extends JPanel
    implements ActionListener, TagParseListener, ListSelectionListener {

    protected JButton playButton;
    protected JTextField urlField;  // replace this with a jcombobox
    protected JList presetList;
    protected DefaultListModel presetListModel;
    // protected JComboBox urlCombo;
    protected JSlider gainSlider;
    protected JTable metadataTable;
    protected MetadataTableModel metadataTableModel;
    protected JTabbedPane outTabs;
    protected JTextArea systemOutArea;
    protected JTextArea systemErrArea;

    protected SimpleMP3DataSource dataSource;
    protected GainControl gainControl;
    protected Player player;
    protected boolean playing = false;

    protected static final String CANNED_URL_FILE = 
        "net/sourceforge/jicyshout/gui/devgui-streams.properties";

    public SimpleDevGUI() {
        super();
        doMyLayout();
        playing = false;
    }

    protected void doMyLayout() {
        // todo: resizing this smaller really sucks
        // (need to give weighty to the table)

        // sigh, big-ass GridBagLayout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        // 0: label, url field, button, horiz. crunch
        // label
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.fill=GridBagConstraints.NONE;
        gbc.anchor=GridBagConstraints.SOUTHEAST;
        add (new JLabel ("URL:"), gbc);
        // url combo
        gbc.gridx=1;
        gbc.weightx=1;
        gbc.anchor=GridBagConstraints.SOUTHWEST;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        urlField = new JTextField (40);
        // urlCombo = new JComboBox();
        // urlCombo.setEditable(true);
        add (urlField, gbc);
        // button
        gbc.gridx=2;
        gbc.weightx=0;
        gbc.fill=GridBagConstraints.NONE;
        playButton = new JButton ("Play");
        playButton.addActionListener (this);
        add (playButton, gbc);
        /*
        // crunch
        gbc.gridx=3;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.weightx=1;
        add (new JPanel(), gbc);
        gbc.weightx=0;
        */

        // 1: preset list
        gbc.gridy=1;
        gbc.gridx=0;
        gbc.anchor=GridBagConstraints.NORTHEAST;
        add (new JLabel("Presets:"), gbc);
        
        gbc.gridx=1;
        gbc.weightx=1;
        gbc.weighty=1;
        gbc.gridwidth=2;
        gbc.fill=GridBagConstraints.BOTH;
        gbc.anchor=GridBagConstraints.SOUTHWEST;
        presetListModel = new DefaultListModel();
        presetList = new JList(presetListModel);
        presetList.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
        presetList.setPrototypeCellValue ("http://222.222.222.222:8888/stream/");
        loadCannedURLs();
        presetList.setVisibleRowCount(4);
        presetList.addListSelectionListener (this);
        JScrollPane presetScroller =
            new JScrollPane (presetList,
                             ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                             ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add (presetScroller, gbc);

        // 2: gain slider
        gbc.gridy=2;
        gbc.gridx=0;
        gbc.anchor=GridBagConstraints.SOUTHEAST;
        add (new JLabel("Gain:"), gbc);
        
        gbc.gridx=1;
        gbc.weightx=1;
        gbc.gridwidth=2;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gainSlider = new JSlider (0, 100, 75);
        gainSlider.addChangeListener (new ChangeListener() {
                public void stateChanged (ChangeEvent ce) {
                    if (ce.getSource() == gainSlider)
                        setGainLevel();
                }
            });
        gainSlider.setEnabled(false);
        add (gainSlider, gbc);

        // 3-9: reserved for future

        // 10: table
        gbc.gridx=0;
        gbc.gridy=10;
        gbc.gridwidth=4;
        gbc.weighty=10;
        gbc.fill=GridBagConstraints.BOTH;
        metadataTableModel = new MetadataTableModel();
        metadataTable = new JTable (metadataTableModel);
        metadataTable.setPreferredScrollableViewportSize (new Dimension (300,200));
        JScrollPane tableScroller =
            new JScrollPane (metadataTable,
                             ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                             ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add (tableScroller, gbc);
        gbc.weighty=0;

        // 20: out/err
        // eh... later

    }


    /** if we're not playing, start, if we are playing, stop.
     */
    public void actionPerformed (ActionEvent evt) {
        if (evt.getSource() != playButton)
            return;
        if (! playing) {
            /// start stuff up
            metadataTableModel.clear();
            String pick = urlField.getText();
            if (pick.trim().equals(""))
                return;
            try {
                URL url = new URL (pick);
                MediaLocator ml = new MediaLocator (url);
                System.out.println ("Got MediaLocator");
                dataSource = new SimpleMP3DataSource (ml, true);
                System.out.println ("Got SimpleMP3DataSource");
                // get any intitial tags
                dataSource.addTagParseListener(this);
                dataSource.connect();
                System.out.println ("Connected DataSource");
                player = Manager.createPlayer (dataSource);
                System.out.println ("Got Player");
                player.setSource (dataSource);
                System.out.println ("Set Player's DataSource");
                player.realize();
                System.out.println ("Called realize()");
                // wait until realized
                while (player.getState() != Controller.Realized) {
                    Thread.sleep (100);
                }
                System.out.println ("Realized");
                setupGainStuff (player);
                // wow, finally start
                player.start();
                System.out.println ("Started");
                playing=true;
                playButton.setText ("Stop");
            } catch (Exception e) {
                e.printStackTrace();
                if (dataSource != null)
                    dataSource.removeTagParseListener(this);
            }
        } else {
            /// shut stuff down
            player.stop();
            player.close();
            player = null;
            dataSource.disconnect();
            dataSource.removeTagParseListener(this);
            dataSource = null;
            playing=false;
            playButton.setText ("Play");
            gainSlider.setEnabled(false);
        }
    } // actionPerformed


    /** Called in actionPerformed to attach a GainControl to 
        the slider.
     */
    protected void setupGainStuff (Player player) {
        if (player == null)
            gainSlider.setEnabled(false);
        gainControl = player.getGainControl();
        if (gainControl != null) {
            gainSlider.setEnabled (true);
            setGainLevel();
        } else {
            gainSlider.setEnabled (false);
        }
    }

    /** Called at startup and when the slider is adjusted to 
        set the gainControl's level.  Slider is 0-100, level is
        0.0f to 1.0f, and this is where that conversion happens.
     */
    protected void setGainLevel () {
        if (gainControl == null)
            return;
        float level = (float) gainSlider.getValue() / 100f;
        gainControl.setLevel (level);
    }

    /** when tags are parsed, add them to the table
     */
    public void tagParsed (TagParseEvent tpe) {
        MP3Tag tag = tpe.getTag();
        metadataTableModel.addTag (tag);
    }

    /** when list selection changes, put its url in the urlField
     */
    public void valueChanged (ListSelectionEvent lse) {
        if (lse.getSource() != presetList)
            return; // never happen
        Object selection = presetList.getSelectedValue();
        if ((selection == null) ||
            (! (selection instanceof Preset)))
            return;
        Preset preset = (Preset) selection;
        urlField.setText (preset.getURLString());
    }

    /** Tries to load a file called n/s/j/g/devgui-streams.properties 
        from the classpath (so it can live in a jar with this class)
        containing some canned stream URLs.  Puts them in the
        presetList.
        <p>
        format of the properties file is
        Name=URL
        though we're only adding the urls into the combo for now
        (name must not use anyting that properties considers a
        delimiter -- :, =, etc.)
     */
    protected void loadCannedURLs () {
        try {
            Properties cannedURLProps = new Properties();
            InputStream cannedURLStream = 
                getClass().getClassLoader().getResourceAsStream(CANNED_URL_FILE);
            cannedURLProps.load (cannedURLStream);
            // now add to presetList
            Enumeration keyEnum = cannedURLProps.keys();
            while (keyEnum.hasMoreElements()) {
                String name = (String) keyEnum.nextElement();
                String url = (String) cannedURLProps.get(name); 
                Preset preset = new Preset (name, url);
                presetListModel.addElement (preset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main (String[] arrrghImAPirate) {
        JFrame frame = new JFrame("jicyshout - dev gui");
        // really just need to shut down in this case
        frame.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
        SimpleDevGUI panel = new SimpleDevGUI();
        frame.getContentPane().add (panel);
        frame.pack();
        frame.setVisible(true);
    }

    public class Preset extends Object {
        protected String name;
        protected String urlString;
        public Preset (String name, String urlString) {
            this.name = name;
            this.urlString = urlString;
        }
        public String getName() { return name; }
        public String getURLString() { return urlString; }
        public String toString() { return name; }
    }

    public class MetadataTableModel extends AbstractTableModel {
        public ArrayList tagList;
        public MetadataTableModel() { 
            super();
            tagList = new ArrayList();
        }
        public int getColumnCount() {
            return 2;
        }
        public int getRowCount () {
            return tagList.size();
        }
        public String getColumnName (int column) {
            return (column == 0 ? "tag name" : "value");
        }
        public Object getValueAt (int row, int column) {
            MP3Tag tag = (MP3Tag) tagList.get (row);
            if (column == 0)
                return tag.getName();
            else
                return tag.getValue().toString();
        }
        /** Replace if it's in the model, add otherwise
         */
        public void addTag (MP3Tag tag) {
            boolean found = false;
            for (int i=0; i<tagList.size(); i++) {
                MP3Tag oldTag = (MP3Tag) tagList.get (i);
                if (oldTag.getName().equals(tag.getName())) {
                    tagList.set (i, tag);
                    found = true;
                    break;
                }
            } // for
            if (! found)
                tagList.add (tag);
            // screw it, we're cheap, update the whole table
            fireTableDataChanged();
        } // addTag
        /** nuke everything
         */
        public void clear() {
            tagList.clear();
            fireTableDataChanged();
        }

    } // inner class
}
