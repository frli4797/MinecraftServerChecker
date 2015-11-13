package message;

import java.io.DataInputStream;
import java.io.IOException;

import dao.Varint;

public class StatusResponseMessage implements Message {

	private int size;

	private DataInputStream data;

	public StatusResponseMessage() {

	}

	@Override
	public byte[] pack() throws IOException {
		return null;
	}

	@Override
	public Object unpack(DataInputStream dataInputStream) throws IOException {
		// Read response
		data = dataInputStream;
		size = Varint.readUnsignedVarInt(data);
		int id = Varint.readUnsignedVarInt(data);

		if (id == -1) {
			throw new IOException("Premature end of stream.");
		}

		if (id != 0x00) { // we want a status response
			throw new IOException("Invalid packetID");
		}

		size = Varint.readUnsignedVarInt(data); // length of json
												// string
		if (size == -1) {
			throw new IOException("Premature end of stream.");
		}

		if (size == 0) {
			throw new IOException("Invalid string length.");
		}

		byte[] in = new byte[size];
		data.readFully(in); // read json string
		String json = new String(in);
		return json;
	}

	@Override
	public byte[] getData() {
		return null;
	}

	@Override
	public int size() {
		return size;
	}

}
