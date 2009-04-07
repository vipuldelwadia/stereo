package stereo.player;

/*
 *	AudioPlayer.java
 *
 *	This file is part of jsresources.org
 */

/*
 * Copyright (c) 1999, 2000 by Matthias Pfisterer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
|<---            this code is formatted to fit into 80 columns             --->|
 */

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer implements LineListener {
	/**	Flag for debugging messages.
	 *	If true, some messages are dumped to the console
	 *	during operation.	
	 */
	private static boolean	DEBUG = false;

	private final SourceDataLine line;
	private final AudioInputStream stream;

	public AudioPlayer(InputStream inputStream) throws UnsupportedAudioFileException, IOException {

		stream = convertAudioStream(inputStream);

		AudioFormat	audioFormat = stream.getFormat();

		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		try
		{
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(audioFormat);
			line.addLineListener(this);
		}
		catch (LineUnavailableException e)
		{
			throw new UnsupportedAudioFileException("AudioPlayer: cannot get SourceDataLine for format " + audioFormat + "; " + e.getMessage());
		}

		if (DEBUG) {
			System.out.println("new AudioPlayer(): line: " + line);
			System.out.println("new AudioPlayer(): line format: " + line.getFormat());
			System.out.println("new AudioPlayer(): line buffer size: " + line.getBufferSize());
		}

	}

	private AudioInputStream convertAudioStream(InputStream inputStream) throws UnsupportedAudioFileException, IOException {

		final int targetSampleSize = 16; //bits

		AudioInputStream audioInputStream;
		if (inputStream instanceof AudioInputStream) {
			audioInputStream = (AudioInputStream)inputStream;
		}
		else {
			audioInputStream = AudioSystem.getAudioInputStream(inputStream);
		}

		if (DEBUG) System.out.println("AudioPlayer.convertAudioStream(): primary AIS: " + audioInputStream);

		AudioFormat	audioFormat = audioInputStream.getFormat();

		if (DEBUG) System.out.println("AudioPlayer.convertAudioStream(): primary format: " + audioFormat);

		DataLine.Info	info = new DataLine.Info(SourceDataLine.class, audioFormat);
		if (!AudioSystem.isLineSupported(info))
		{
			AudioFormat	sourceFormat = audioFormat;
			AudioFormat	targetFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED,
					audioFormat.getSampleRate(),
					targetSampleSize,
					audioFormat.getChannels(),
					audioFormat.getChannels() * (targetSampleSize / 8),
					audioFormat.getSampleRate(),
					false /*is bigendian*/);
			if (DEBUG)
			{
				System.out.println("AudioPlayer.convertAudioStream(): source format: " + sourceFormat);
				System.out.println("AudioPlayer.convertAudioStream(): target format: " + targetFormat);
			}
			audioInputStream = AudioSystem.getAudioInputStream(targetFormat, audioInputStream);

			if (DEBUG) System.out.println("AudioPlayer.convertAudioStream(): converted AIS: " + audioInputStream);
			if (DEBUG) System.out.println("AudioPlayer.convertAudioStream(): converted format: " + audioFormat);
		}

		return audioInputStream;
	}

	public void start() throws IOException {
		/*
		 *	Still not enough. The line now can receive data,
		 *	but will not pass them on to the audio output device
		 *	(which means to your sound card). This has to be
		 *	activated.
		 */
		line.start();

		int	read = 0;
		byte[]	abData = new byte[128000];
		if (DEBUG) System.out.println("AudioPlayer.main(): starting main loop");
		while (read != -1)
		{
			read = stream.read(abData, 0, abData.length);
			if (read > 0) line.write(abData, 0, read);
		}
		if (DEBUG) System.out.println("AudioPlayer.main(): finished main loop");

		line.drain();
		line.close();
	}

	public int getPosition() {
		return (int)(line.getMicrosecondPosition() / 1000);
	}
	
	public void pause() {
		System.out.println("player: pause");
		line.stop();
	}

	public void play() {
		System.out.println("player: play");
		line.start();
	}
	
	public boolean isPlaying() {
		return line.isActive();
	}

	public void stop() {
		System.out.println("player: stop");
		try {
			if (stream != null) stream.close();
		}
		catch (IOException ex) {}
		line.flush();
		line.close();
	}

	@Override
	public void update(LineEvent ev) {
		System.out.println(ev.toString());
	}

}

/*** AudioPlayer.java ***/