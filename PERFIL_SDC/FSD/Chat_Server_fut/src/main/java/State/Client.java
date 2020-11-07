package State;

import spullara.nio.channels.FutureSocketChannel;

import java.nio.channels.AsynchronousSocketChannel;

public class Client {
    public final FutureSocketChannel socket;
    public int messageId;

    public Client(FutureSocketChannel socket, int messageId){
        this.socket = socket;
        this.messageId = messageId;
    }
}
