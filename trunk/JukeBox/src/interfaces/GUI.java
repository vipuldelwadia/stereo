package interfaces;
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

import player.Controller;


public class GUI {

	private Table trackList;
	protected Shell shell;

	private Button playButton;
	

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
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private void play(){
		playButton.setText("Pause");
		Controller.getInstance().playTrack();
	}

	private void pause(){
		playButton.setText("Play");
		Controller.getInstance().pauseTrack();
	}
	
	private void skipTrack(){
		Controller.getInstance().skipTrack();
	}
	
	private void updateVolume(int volume) {
		Controller.getInstance().changeVolume(volume);
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
		formData.right = new FormAttachment(0, 235);
		formData.left = new FormAttachment(0, 115);
		playButton.setLayoutData(formData);
		playButton.setText("Play");

		final Button skipButton = new Button(shell, SWT.NONE);
		skipButton.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(final SelectionEvent e) {
				skipTrack();
			}
		});
		final FormData formData_1 = new FormData();
		formData_1.bottom = new FormAttachment(playButton, 30, SWT.TOP);
		formData_1.top = new FormAttachment(playButton, 0, SWT.TOP);
		formData_1.right = new FormAttachment(0, 385);
		formData_1.left = new FormAttachment(0, 265);
		skipButton.setLayoutData(formData_1);
		skipButton.setText("Skip");

		final ProgressBar progressBar = new ProgressBar(shell, SWT.NONE);
		final FormData formData_2 = new FormData();
		formData_2.right = new FormAttachment(100, -5);
		formData_2.bottom = new FormAttachment(0, 142);
		formData_2.top = new FormAttachment(0, 112);
		formData_2.left = new FormAttachment(0, 10);
		progressBar.setLayoutData(formData_2);

		final Scale scale;
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

		final Label songnameLabel = new Label(shell, SWT.NONE);
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

		final TableColumn albumColumn = new TableColumn(trackList, SWT.NONE);
		albumColumn.setWidth(100);
		albumColumn.setText("Album");

		final TableColumn artistColumn = new TableColumn(trackList, SWT.NONE);
		artistColumn.setWidth(100);
		artistColumn.setText("Artist");

		final TableColumn titleColumn = new TableColumn(trackList, SWT.NONE);
		titleColumn.setWidth(100);
		titleColumn.setText("Title");


		//
	}

}
