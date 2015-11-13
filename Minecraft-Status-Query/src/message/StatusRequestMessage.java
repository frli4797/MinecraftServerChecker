package message;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StatusRequestMessage implements Message {

	private ByteArrayOutputStream bos;

	/*
	 * (non-Javadoc)
	 * 
	 * @see message.Message#pack()
	 */
	@Override
	public byte[] pack() throws IOException {
		bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		dos.writeByte(0x00); // packet id for ping

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
		return 0x01;
	}

	@Override
	public byte[] getData() {
		return bos.toByteArray();
	}

}
