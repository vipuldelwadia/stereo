package writer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Date;

import util.command.Image;
import util.command.Song;
import api.Response;
import api.Writer;
import dmap.DACPWriter;
import dmap.node.PageNode;

public class DACPResponseGenerator {

	public void success(Response content, OutputStream output) throws IOException {

		PrintStream out = new PrintStream(output);
		byte[] data = null;
		
		out.printf("HTTP/1.1 %s\r\n", content.statusText());

		if (content == null);
		else if (content instanceof PageNode) {
			PageNode page = (PageNode)content;

			out.print("Content-Type: " + page.contentType() + "\r\n");
			out.print("Content-Length: "+page.length()+"\r\n");
			out.print("Date: "+DateFormat.getDateInstance().format(new Date())+"\r\n");

			data = page.text();
		}
		else if (content instanceof Image) {
			
			byte[] image = ((Image)content).image();
			
			out.print("Content-Type: image/jpg\r\n");
			out.print("Content-Length: "+image.length+"\r\n");
			out.print("Date: "+DateFormat.getDateInstance().format(new Date())+"\r\n");
			
			data = image;
		}
		else if (content instanceof Song) {
			
			byte[] song = ((Song)content).song();
			
			out.print("Content-Type: audio/mpeg\r\n");
			out.print("Content-Length: "+song.length+"\r\n");
			out.print("Date: "+DateFormat.getDateInstance().format(new Date())+"\r\n");
			
			data = song;
		}
		else if (content.type() != null) {

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			Writer wr = new DACPWriter(os);
			wr.appendNode(content);
			
			out.print("Content-Type: application/x-dmap-tagged\r\n");
			out.print("Content-Length: "+os.size()+"\r\n");
			out.print("DAAP-Server: memphis-dj-0.2\r\n");
			out.print("Date: "+DateFormat.getDateInstance().format(new Date())+"\r\n");

			data = os.toByteArray();
		}

		out.print("\r\n");
		
		if (data != null) {
			out.write(data);
		}
		
		out.flush();
	}

	public void error(String msg, OutputStream output) {
		PrintStream out = new PrintStream(output);
		out.printf("HTTP/1.1 %s\r\n\r\n", msg);
		out.flush();
	}

}
