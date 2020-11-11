package C;

import F.Server;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.nio.ByteBuffer;

public class Client {
    public static void main(String[] args) throws Exception {
        try (ZContext context = new ZContext()) {
            //  Socket to send messages on
            ZMQ.Socket sender = context.createSocket(SocketType.PUSH);
            sender.connect(Server.BOUNDED_ADDRESS);
            ZMQ.Socket receiver = context.createSocket(SocketType.PULL);
            receiver.connect(S.Server.BOUNDED_ADDRESS_ANS);

            for(int i=0; i<10; i++) {
                sender.send(ByteBuffer.allocate(4).putInt(i).array());
            }
            System.out.println("All work sent.");
            while (true)
                System.out.println(ByteBuffer.wrap(receiver.recv()).getInt());
        }
    }

}
