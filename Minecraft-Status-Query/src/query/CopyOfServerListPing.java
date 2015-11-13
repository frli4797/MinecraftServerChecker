package query;

import java.io.IOException;
import java.net.InetSocketAddress;

import message.HandshakeMessage;
import message.Message;
import message.PingMessage;
import message.PingResponseMessage;
import message.StatusRequestMessage;
import message.StatusResponseMessage;
import processor.MinecraftMessageProcessor;

import com.google.gson.Gson;

import dao.StatusResponse;
import exception.MinecraftServerException;

//TODO: State design pattern?!

/**
 * 
 */
public class CopyOfServerListPing {

	private InetSocketAddress host;
	private int timeout = 7000;
	private Gson gson = new Gson();

	private MinecraftMessageProcessor processor;

	public CopyOfServerListPing(InetSocketAddress host) {
		processor = new MinecraftMessageProcessor(host);
		this.host = host;
	}

	void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	int getTimeout() {
		return this.timeout;
	}

	public StatusResponse fetchData() throws MinecraftServerException {

		StatusResponse response = new StatusResponse();
		try {
			processor.initialize();
			handshake();

			response = readData();

		} catch (Exception e) {
			throw new MinecraftServerException("Could not get server status.",
					e);
		} finally {
			processor.close();
		}

		return response;
	}

	private StatusResponse readData() throws IOException {
		// Write Request
		Message m = new StatusRequestMessage();
		m.pack();
		processor.sendMessage(m);

		String json = (String) processor
				.recieveMessage(StatusResponseMessage.class);

		long now = System.currentTimeMillis();
		Message pingMessage = new PingMessage(now);
		pingMessage.pack();
		processor.sendMessage(pingMessage);

		long pingtime = (long) processor
				.recieveMessage(PingResponseMessage.class);

		StatusResponse response = gson.fromJson(json, StatusResponse.class);
		response.setTime((int) (now - pingtime));
		response.setOnline(true);
		return response;
	}

	private void handshake() throws IOException {

		Message m = new HandshakeMessage(host);
		m.pack();
		processor.sendMessage(m);
	}

	public static void main(String[] args) {
		CopyOfServerListPing query = new CopyOfServerListPing(
				new InetSocketAddress("access.jagare-lilja.se", 25565));
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
