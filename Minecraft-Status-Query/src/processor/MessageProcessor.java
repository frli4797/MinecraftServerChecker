package processor;

import java.io.IOException;

import message.Message;

public interface MessageProcessor {

	public void sendMessage(Message mess) throws IOException;

	public Object recieveMessage(Class<?> clazz) throws IOException;

}
