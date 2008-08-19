package temp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.media.Manager;
import javax.media.Time;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.InputSourceStream;
import javax.media.protocol.PullDataSource;
import javax.media.protocol.PullSourceStream;

public class Player {

	public static void main(String args[]) {

		try {
			File file = new File(
					"temp/rappin.wav");
			final InputStream is = new FileInputStream(file);
			final ContentDescriptor content = new ContentDescriptor(
					ContentDescriptor.CONTENT_UNKNOWN);

			PullDataSource src = new PullDataSource() {

				@Override
				public PullSourceStream[] getStreams() {
					PullSourceStream[] streams = new PullSourceStream[1];
					streams[0] = new InputSourceStream(is, content);
					return streams;
				}

				public void connect() throws IOException {
				}

				public void disconnect() {
				}

				public String getContentType() {
					return null;
				}

				public Object getControl(String arg0) {
					return null;
				}

				public Object[] getControls() {
					return null;
				}

				public Time getDuration() {
					return Time.TIME_UNKNOWN;
				}

				public void start() throws IOException {
				}

				public void stop() throws IOException {
				}

			};

			javax.media.Player player = Manager.createPlayer(src);

			player.start();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}