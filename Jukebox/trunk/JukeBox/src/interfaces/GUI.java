package interfaces;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import player.Controller;
import playlist.Playlist;
import playlist.Track;


public class GUI {

	private Table trackList;
	protected Shell shell;

	private Button playButton;
	private Scale scale;
	private Label songnameLabel;
	private TableColumn albumColumn;
	private TableColumn artistColumn;
	private TableColumn genreColumn;
	private TableColumn titleColumn;
	private TableColumn timeColumn;

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			GUI window = new GUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window
	 */
	public void open() {
		final Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		// yucky hacky code to see that it works ok
		// TODO remove this
		List<Track> tracks = new ArrayList<Track>();
		for (int i = 0; i < 20; i++) tracks.add(new Track());
		populatePlaylist(new Playlist(tracks));
		playingTrackUpdated(new Track());
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private void play(){
		Controller.getInstance().playTrack();
	}

	private void pause(){
		Controller.getInstance().pauseTrack();
	}
	
	private void skipTrack(){
		Controller.getInstance().skipTrack();
	}
	
	private void updateVolume(int volume) {
		Controller.getInstance().changeVolume(volume);
	}
	
	/**
	 * sets the play/pause button to play
	 *
	 */
	public void trackPlayed() {
		playButton.setText("Pause");
	}
	
	/**
	 * sets the play/pause button to pause
	 */
	public void trackPaused() {
		playButton.setText("Play");
	}
	
	/**
	 * sets the currently playing track to the given value
	 * @throws NullPointerException if t is null
	 */
	public void playingTrackUpdated(Track t) {
		if (t == null) throw new NullPointerException("t must not be null");
		songnameLabel.setText(t.toString());
	}
	
	/**
	 * sets the current playlist to the given value
	 * @throws NullPointerException if p is null
	 */
	public void playlistUpdated(Playlist p) {
		if (p == null) throw new NullPointerException("p must not be null");
		populatePlaylist(p);
	}
	
	/**
	 * sets the volume to the given value
	 * @throws IllegalArgumentException if volume is < 0 or > 10
	 */
	public void volumeUpdated(int volume) {
		if (volume < 0 || volume > 10) throw new IllegalArgumentException("volume must be between 0 and 10");
		scale.setSelection(volume);
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setLayout(new FormLayout());
		shell.setSize(562, 568);
		shell.setText("SWT Application");

		playButton = new Button(shell, SWT.NONE);
		playButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if(playButton.getText().equals("Pause")){
					pause();	
				}else{
					play();
				}
			}
		});

		final FormData formData = new FormData();
		formData.left = new FormAttachment(0, 30);
		formData.right = new FormAttachment(0, 150);
		playButton.setLayoutData(formData);
		playButton.setText("Play");

		final Button skipButton = new Button(shell, SWT.NONE);
		skipButton.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(final SelectionEvent e) {
				skipTrack();
			}
		});
		final FormData formData_1 = new FormData();
		formData_1.left = new FormAttachment(0, 275);
		formData_1.right = new FormAttachment(0, 395);
		skipButton.setLayoutData(formData_1);
		skipButton.setText("Skip");

		final ProgressBar progressBar = new ProgressBar(shell, SWT.NONE);
		final FormData formData_2 = new FormData();
		formData_2.right = new FormAttachment(100, -5);
		formData_2.bottom = new FormAttachment(0, 142);
		formData_2.top = new FormAttachment(0, 112);
		formData_2.left = new FormAttachment(0, 10);
		progressBar.setLayoutData(formData_2);

		scale = new Scale(shell, SWT.NONE);
		formData.top = new FormAttachment(scale, -35, SWT.TOP);
		formData.bottom = new FormAttachment(scale, -5, SWT.TOP);
		final FormData formData_3 = new FormData();
		formData_3.right = new FormAttachment(100, -5);
		formData_3.bottom = new FormAttachment(0, 76);
		formData_3.top = new FormAttachment(0, 46);
		formData_3.left = new FormAttachment(0, 67);
		scale.setLayoutData(formData_3);
		scale.setMinimum(0);
		scale.setMaximum(10);
		scale.setIncrement(1);
		scale.addListener(SWT.Selection, new Listener() {
			private int current = 0;
		      public void handleEvent(Event event) {
		        int perspectiveValue = scale.getSelection() + scale.getMinimum();
		        if (perspectiveValue != current) {
		        	updateVolume(perspectiveValue);
		        	current = perspectiveValue;
		        }
		      }
		    });

		final Label volumeLabel = new Label(shell, SWT.NONE);
		final FormData formData_4 = new FormData();
		formData_4.bottom = new FormAttachment(0, 65);
		formData_4.top = new FormAttachment(0, 46);
		formData_4.right = new FormAttachment(0, 61);
		formData_4.left = new FormAttachment(0, 10);
		volumeLabel.setLayoutData(formData_4);
		volumeLabel.setText("Volume");

		songnameLabel = new Label(shell, SWT.NONE);
		final FormData formData_5 = new FormData();
		formData_5.right = new FormAttachment(progressBar, 0, SWT.RIGHT);
		formData_5.bottom = new FormAttachment(0, 112);
		formData_5.top = new FormAttachment(0, 82);
		formData_5.left = new FormAttachment(0, 10);
		songnameLabel.setLayoutData(formData_5);
		songnameLabel.setText("SongName");

		trackList = new Table(shell, SWT.BORDER);
		final FormData formData_6 = new FormData();
		formData_6.right = new FormAttachment(100, -5);
		formData_6.bottom = new FormAttachment(100, -5);
		formData_6.top = new FormAttachment(progressBar, 5, SWT.BOTTOM);
		formData_6.left = new FormAttachment(progressBar, 0, SWT.LEFT);
		trackList.setLayoutData(formData_6);
		trackList.setLinesVisible(true);
		trackList.setHeaderVisible(true);

		titleColumn = new TableColumn(trackList, SWT.NONE);
		titleColumn.setWidth(100);
		titleColumn.setText("Title");

		artistColumn = new TableColumn(trackList, SWT.NONE);
		artistColumn.setWidth(100);
		artistColumn.setText("Artist");

		albumColumn = new TableColumn(trackList, SWT.NONE);
		albumColumn.setWidth(100);
		albumColumn.setText("Album");

		genreColumn = new TableColumn(trackList, SWT.NONE);
		genreColumn.setWidth(100);
		genreColumn.setText("Genre");

		timeColumn = new TableColumn(trackList, SWT.NONE);
		timeColumn.setWidth(100);
		timeColumn.setText("Time");

		
		/////////////////////////////////////////////////////////////////////
		////////////////////// T E M P //////////////////////////////////////
		/////////////////////////////////////////////////////////////////////
		// TODO remove this when the play/pause toggles properly!
		Button pauseButton;
		pauseButton = new Button(shell, SWT.NONE);
		formData_1.top = new FormAttachment(pauseButton, -30, SWT.BOTTOM);
		formData_1.bottom = new FormAttachment(pauseButton, 0, SWT.BOTTOM);
		pauseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				pause();
			}
		});
		final FormData formData_7 = new FormData();
		formData_7.top = new FormAttachment(playButton, -28, SWT.BOTTOM);
		formData_7.bottom = new FormAttachment(playButton, 0, SWT.BOTTOM);
		formData_7.right = new FormAttachment(playButton, 120, SWT.RIGHT);
		formData_7.left = new FormAttachment(playButton, 5, SWT.RIGHT);
		pauseButton.setLayoutData(formData_7);
		pauseButton.setText("Pause");
	}
	
	private void populatePlaylist(Playlist p) {
		for (Track t : p.getPlaylist()) {
			TableItem track = new TableItem(trackList, SWT.NONE);
			track.setText(0, t.getTitle());
			track.setText(1, t.getAlbum());
			track.setText(2, t.getArtist());
			track.setText(3, t.getGenre());
			track.setText(4, new Integer(t.getTime()).toString());
		}
	}

}
