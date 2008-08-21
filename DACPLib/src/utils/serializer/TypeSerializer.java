package utils.serializer;

import java.io.OutputStream;

public interface TypeSerializer {
	public void serialize(Object obj, OutputStream out);
}
