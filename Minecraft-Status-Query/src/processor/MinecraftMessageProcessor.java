package processor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import message.Message;

public class MinecraftMessageProcessor implements MessageProcessor {

	private InetSocketAddress host;
	private int timeout = 7000;
	private DataOutputStream dataOutputStream;
	private DataInputStream dataInputStream;
	private Socket socket;

	public MinecraftMessageProcessor(InetSocketAddress address) {
		host = address;
	}

	public void initialize() throws IOException {
		socket = new Socket();

		socket.setSoTimeout(this.timeout);
		socket.connect(host, timeout);

		dataOutputStream = new DataOutputStream(socket.getOutputStream());
		dataInputStream = new DataInputStream(socket.getInputStream());
	}

	@Override
	public void sendMessage(Message mess) throws IOException {
		dataOutputStream.write(mess.size());
		dataOutputStream.write(mess.getData());
	}

	@Override
	public Object recieveMessage(Class<?> clazz) throws IOException {
		Message m = null;

		try {
			m = (Message) clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m.unpack(dataInputStream);
	}

	public void close() {
		try {
			if (dataOutputStream != null)
				dataOutputStream.close();
			if (dataInputStream != null)
				dataInputStream.close();
			socket.close();
		} catch (IOException e) {
		}
	}

}
