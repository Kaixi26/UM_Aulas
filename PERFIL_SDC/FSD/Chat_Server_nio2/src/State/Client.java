package State;

import java.nio.channels.AsynchronousSocketChannel;

public class Client {
    public final AsynchronousSocketChannel socket;
    public int messageId;

    public Client(AsynchronousSocketChannel socket, int messageId){
        this.socket = socket;
        this.messageId = messageId;
    }
}
