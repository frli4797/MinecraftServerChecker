package message;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

import dao.Varint;

public class HandshakeMessage implements Message {

	private InetSocketAddress host;

	private ByteArrayOutputStream bos;

	public HandshakeMessage(InetSocketAddress address) {
		host = address;
	}

	@Override
	public int size() {
		return bos.size();
	}

	@Override
	public byte[] pack() throws IOException {
		bos = new ByteArrayOutputStream();
		DataOutputStream payload = new DataOutputStream(bos);

		payload.writeByte(0x00); // packet id for handshake
		Varint.writeUnsignedVarInt(4, payload); // protocol version
		Varint.writeUnsignedVarInt(this.host.getHostString().length(), payload); // host
																					// length

		payload.writeBytes(this.host.getHostString()); // host string
		payload.writeShort(host.getPort()); // port
		Varint.writeUnsignedVarInt(1, payload); // state (1 for handshake)
		// Varint.writeUnsignedVarInt(bos.size(), payload); // Write packet size

		return bos.toByteArray();
	}

	@Override
	public Object unpack(DataInputStream data) throws IOException {
		return null;
	}

	@Override
	public byte[] getData() {
		return bos.toByteArray();
	}

}
