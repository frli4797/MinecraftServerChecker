package query;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import com.google.gson.Gson;

import dao.StatusResponse;
import dao.Varint;
import exception.MinecraftServerException;

/**
 * 
 * @author zh32 <zh32 at zh32.de>
 */
public class ServerListPing {

	private InetSocketAddress host;
	private int timeout = 7000;
	private Gson gson = new Gson();
	private DataOutputStream dataOutputStream;
	private DataInputStream dataInputStream;
	private Socket socket;

	public ServerListPing(InetSocketAddress host) {
		super();
		this.host = host;
	}

	public void setAddress(InetSocketAddress host) {
		this.host = host;
	}

	public InetSocketAddress getAddress() {
		return this.host;
	}

	void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	int getTimeout() {
		return this.timeout;
	}

	private int readVarInt(DataInputStream in) throws IOException {
		int i = 0;
		int j = 0;
		while (true) {
			int k = in.readByte();
			i |= (k & 0x7F) << j++ * 7;
			if (j > 5)
				throw new RuntimeException("VarInt too big");
			if ((k & 0x80) != 128)
				break;
		}
		return i;
	}

	private void writeVarInt(DataOutputStream out, int paramInt)
			throws IOException {
		while (true) {
			if ((paramInt & 0xFFFFFF80) == 0) {
				out.writeByte(paramInt);
				return;
			}

			out.writeByte(paramInt & 0x7F | 0x80);
			paramInt >>>= 7;
		}
	}

	public StatusResponse fetchData() {

		StatusResponse response = new StatusResponse();
		try {
			initialize();
			handshake();

			response = readData();

		} catch (Exception e) {
			new MinecraftServerException("Could not get server status.", e);
		} finally {
			cleanup();
		}

		return response;
	}

	private void cleanup() {
		try {
			if (dataOutputStream != null)
				dataOutputStream.close();
			if (dataInputStream != null)
				dataInputStream.close();
			socket.close();
		} catch (IOException e) {
		}
	}

	private StatusResponse readData() throws IOException {
		int size = readVarInt(dataInputStream); // size of packet
		//int id = readVarInt(dataInputStream); // packet id

		int id = Varint.readSignedVarInt(dataInputStream);
		
		if (id == -1) {
			throw new IOException("Premature end of stream.");
		}

		if (id != 0x00) { // we want a status response
			throw new IOException("Invalid packetID");
		}
		int length = readVarInt(dataInputStream); // length of json string

		if (length == -1) {
			throw new IOException("Premature end of stream.");
		}

		if (length == 0) {
			throw new IOException("Invalid string length.");
		}

		byte[] in = new byte[length];
		dataInputStream.readFully(in); // read json string
		String json = new String(in);

		long now = System.currentTimeMillis();
		dataOutputStream.writeByte(0x09); // size of packet
		dataOutputStream.writeByte(0x01); // 0x01 for ping
		dataOutputStream.writeLong(now); // time!?

		readVarInt(dataInputStream);
		id = readVarInt(dataInputStream);
		if (id == -1) {
			throw new IOException("Premature end of stream.");
		}

		if (id != 0x01) {
			throw new IOException("Invalid packetID");
		}
		long pingtime = dataInputStream.readLong(); // read response

		StatusResponse response = gson.fromJson(json, StatusResponse.class);
		response.setTime((int) (now - pingtime));
		return response;
	}

	private void handshake() throws IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream handshake = new DataOutputStream(b);
		handshake.writeByte(0x00); // packet id for handshake
		writeVarInt(handshake, 4); // protocol version
		writeVarInt(handshake, this.host.getHostString().length()); // host
																	// length
		handshake.writeBytes(this.host.getHostString()); // host string
		handshake.writeShort(host.getPort()); // port
		writeVarInt(handshake, 1); // state (1 for handshake)

		writeVarInt(dataOutputStream, b.size()); // prepend size
		dataOutputStream.write(b.toByteArray()); // write handshake packet

		dataOutputStream.writeByte(0x01); // size is only 1
		dataOutputStream.writeByte(0x00); // packet id for ping
	}

	private Socket initialize() throws SocketException, IOException {
		socket = new Socket();

		socket.setSoTimeout(this.timeout);
		socket.connect(host, timeout);

		dataOutputStream = new DataOutputStream(socket.getOutputStream());
		dataInputStream = new DataInputStream(socket.getInputStream());
		return socket;
	}

	public static void main(String[] args) {
		ServerListPing query = new ServerListPing(new InetSocketAddress(
				"access.jagare-lilja.se", 25565));
		StatusResponse status = null;
		try {
			status = query.fetchData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (status.isOnline())
			System.out.println(status.toString());
		else
			System.out.println("Offline");

	}
}
