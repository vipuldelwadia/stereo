package player;

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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

public class AudioPlayer {

	private static final int	DEFAULT_EXTERNAL_BUFFER_SIZE = 128000;
	
	public static void play(final AudioInputStream stream) {
		
		AudioInputStream audioInputStream = stream;

		/** Sample size value to use in conversion.
	    If a conversion of the AudioInputStream is done,
	    this values is used as sample size in the target
	    AudioFormat.
	    The default value can be altered by the command line
	    option "-S".
		 */
		final int	nSampleSizeInBits = 16;

		final String	strMixerName = null;

		final int	nExternalBufferSize = AudioPlayer.DEFAULT_EXTERNAL_BUFFER_SIZE;

		final int	nInternalBufferSize = AudioSystem.NOT_SPECIFIED;

		/*
		 *	From the AudioInputStream, i.e. from the sound file,
		 *	we fetch information about the format of the
		 *	audio data.
		 *	These information include the sampling frequency,
		 *	the number of
		 *	channels and the size of the samples.
		 *	These information
		 *	are needed to ask Java Sound for a suitable output line
		 *	for this audio stream.
		 */
		AudioFormat	audioFormat = audioInputStream.getFormat();

		final DataLine.Info	info = new DataLine.Info(SourceDataLine.class,
							 audioFormat, nInternalBufferSize);
		boolean	bIsSupportedDirectly = AudioSystem.isLineSupported(info);
		if (!bIsSupportedDirectly)
		{
			final AudioFormat	sourceFormat = audioFormat;
			final AudioFormat	targetFormat = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED,
				sourceFormat.getSampleRate(),
				nSampleSizeInBits,
				sourceFormat.getChannels(),
				sourceFormat.getChannels() * (nSampleSizeInBits / 8),
				sourceFormat.getSampleRate(),
				false);
			audioInputStream = AudioSystem.getAudioInputStream(targetFormat, audioInputStream);
			audioFormat = audioInputStream.getFormat();
		}

		final SourceDataLine line = getSourceDataLine(strMixerName, audioFormat, nInternalBufferSize);
		if (line == null)
		{
			System.err.println("AudioPlayer: cannot get SourceDataLine for format " + audioFormat);
			return;
		}

		/*
		 *	Still not enough. The line now can receive data,
		 *	but will not pass them on to the audio output device
		 *	(which means to your sound card). This has to be
		 *	activated.
		 */
		line.start();

		/*
		 *	Ok, finally the line is prepared. Now comes the real
		 *	job: we have to write data to the line. We do this
		 *	in a loop. First, we read data from the
		 *	AudioInputStream to a buffer. Then, we write from
		 *	this buffer to the Line. This is done until the end
		 *	of the file is reached, which is detected by a
		 *	return value of -1 from the read method of the
		 *	AudioInputStream.
		 */
		int	nBytesRead = 0;
		final byte[]	abData = new byte[nExternalBufferSize];
		do
		{
			try
			{
				nBytesRead = audioInputStream.read(abData, 0, abData.length);
			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
			
			//log.debug("read from AudioInputStream (bytes): " + nBytesRead);
			
			if (nBytesRead > 0)
			{
				//final int nBytesWritten = 
				line.write(abData, 0, nBytesRead);
				
				//log.debug("written to SourceDataLine (bytes): " + nBytesWritten);
			}
			
		} while (nBytesRead > 0);

        System.err.println("finished main loop");

		/*
		 *	Wait until all data is played.
		 *	This is only necessary because of the bug noted below.
		 *	(If we do not wait, we would interrupt the playback by
		 *	prematurely closing the line and exiting the VM.)
		 *
		 *	Thanks to Margie Fitch for bringing me on the right
		 *	path to this solution.
		 */
        System.err.println("before drain");
		
		line.drain();

		/*
		 *	All data are played. We can close the shop.
		 */
        System.err.println("AudioPlayer.main(): before close");
		
		line.close();

	}


	// TODO: maybe can used by others. AudioLoop?
	// In this case, move to AudioCommon.
	private static SourceDataLine getSourceDataLine(final String strMixerName,
							final AudioFormat audioFormat,
							final int nBufferSize)
	{
		/*
		 *	Asking for a line is a rather tricky thing.
		 *	We have to construct an Info object that specifies
		 *	the desired properties for the line.
		 *	First, we have to say which kind of line we want. The
		 *	possibilities are: SourceDataLine (for playback), Clip
		 *	(for repeated playback)	and TargetDataLine (for
		 *	 recording).
		 *	Here, we want to do normal playback, so we ask for
		 *	a SourceDataLine.
		 *	Then, we have to pass an AudioFormat object, so that
		 *	the Line knows which format the data passed to it
		 *	will have.
		 *	Furthermore, we can give Java Sound a hint about how
		 *	big the internal buffer for the line should be. This
		 *	isn't used here, signaling that we
		 *	don't care about the exact size. Java Sound will use
		 *	some default value for the buffer size.
		 */
		SourceDataLine	line = null;
		final DataLine.Info	info = new DataLine.Info(SourceDataLine.class,
							 audioFormat, nBufferSize);
		try
		{
			if (strMixerName != null)
			{
				final Mixer.Info	mixerInfo = AudioCommon.getMixerInfo(strMixerName);
				if (mixerInfo == null)
				{
                    System.err.println("AudioPlayer: mixer not found: " + strMixerName);
					return null;
				}
				final Mixer	mixer = AudioSystem.getMixer(mixerInfo);
				line = (SourceDataLine) mixer.getLine(info);
			}
			else
			{
				line = (SourceDataLine) AudioSystem.getLine(info);
			}

			/*
			 *	The line is there, but it is not yet ready to
			 *	receive audio data. We have to open the line.
			 */
			line.open(audioFormat, nBufferSize);
		}
		catch (final LineUnavailableException e)
		{
            System.err.println("line not available" + e);
		}
		catch (final Exception e)
		{
			System.err.println("error" + e);
		}
		return line;
	}

}



/*** AudioPlayer.java ***/

