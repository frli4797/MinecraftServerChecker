package message;

import java.io.DataInputStream;
import java.io.IOException;

public interface Message {

	public byte[] pack() throws IOException;

	public Object unpack(DataInputStream input) throws IOException;

	public byte[] getData();

	public int size();

}