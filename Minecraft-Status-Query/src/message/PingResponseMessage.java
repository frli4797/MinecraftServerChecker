package message;

import java.io.DataInputStream;
import java.io.IOException;

import dao.Varint;

public class PingResponseMessage implements Message {

	private int size;

	private DataInputStream data;

	@Override
	public byte[] pack() throws IOException {
		return null;
	}

	@Override
	public Object unpack(DataInputStream input) throws IOException {
		data = input;

		size = Varint.readUnsignedVarInt(data);
		int id = Varint.readUnsignedVarInt(data);
		if (id == -1) {
			throw new IOException("Premature end of stream.");
		}

		if (id != 0x01) {
			throw new IOException("Invalid packetID");
		}
		long pingtime = data.readLong(); // read response

		return pingtime;
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
