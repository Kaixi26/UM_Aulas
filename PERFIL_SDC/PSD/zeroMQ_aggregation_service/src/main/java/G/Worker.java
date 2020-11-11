package G;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.nio.ByteBuffer;

public class Worker {
    public static void main(String[] args) throws Exception {
        final ZContext context = new ZContext();
        ZMQ.Socket receiver = context.createSocket(SocketType.PULL);
        receiver.connect(Server.BOUNDED_ADDRESS_WORKERS);

        ZMQ.Socket sender = context.createSocket(SocketType.PUSH);
        sender.connect(Server.BOUNDED_ADDRESS_WORKERS_FIN);

        while(true){
            byte[] msg = receiver.recv();
            System.out.println(ByteBuffer.wrap(msg).getInt());
            Thread.sleep(100);
            sender.send(msg);
        }
    }
}
