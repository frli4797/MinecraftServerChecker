package query;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import message.HandshakeMessage;
import message.Message;
import message.PingMessage;
import message.PingResponseMessage;
import message.StatusRequestMessage;
import message.StatusResponseMessage;

import com.google.gson.Gson;

import dao.StatusResponse;
import dao.Varint;
import exception.MinecraftServerException;

//TODO: State design pattern?!

/**
 * 
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

	public InetSocketAddress getAddress() {
		return this.host;
	}

	void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	int getTimeout() {
		return this.timeout;
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
		// Write Request
		Message m = new StatusRequestMessage();
		m.pack();

		dataOutputStream.write(m.size());
		dataOutputStream.write(m.getData());

		Message responseMess = new StatusResponseMessage();
		String json = (String) responseMess.unpack(dataInputStream);

		long now = System.currentTimeMillis();
		Message pingMessage = new PingMessage(now);
		pingMessage.pack();
		dataOutputStream.write(pingMessage.size());
		dataOutputStream.write(pingMessage.getData()); // time!?

		Message pingResponse = new PingResponseMessage();
		long pingtime = (long) pingResponse.unpack(dataInputStream);

		StatusResponse response = gson.fromJson(json, StatusResponse.class);
		response.setTime((int) (now - pingtime));
		response.setOnline(true);
		return response;
	}

	private void handshake() throws IOException {

		Message m = new HandshakeMessage(host);
		m.pack();
		Varint.writeUnsignedVarInt(m.size(), dataOutputStream);
		dataOutputStream.write(m.getData());
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
