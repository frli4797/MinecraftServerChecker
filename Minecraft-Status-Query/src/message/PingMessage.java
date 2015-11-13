package message;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PingMessage implements Message {
	private ByteArrayOutputStream bos;
	private long time;

	public PingMessage(long time) {
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see message.Message#pack()
	 */
	@Override
	public byte[] pack() throws IOException {
		bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		dos.writeByte(0x01); // packet id for ping
		dos.writeLong(time);
		return bos.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see message.Message#unpack()
	 */
	@Override
	public Object unpack(DataInputStream data) throws IOException {
		return null;

	}

	@Override
	public int size() {
		return bos.size();
	}

	@Override
	public byte[] getData() {
		return bos.toByteArray();
	}

}
