package F;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.nio.ByteBuffer;

public class Worker extends Thread {
    ZContext context;
    final int n;

    Worker(ZContext context, int n) {
        this.context = context;
        this.n = n;
    }

    public void run() {
        ZMQ.Socket receiver = context.createSocket(SocketType.PULL);
        receiver.connect(Server.BOUNDED_ADDRESS_WORKERS);
        ZMQ.Socket sender = context.createSocket(SocketType.PUSH);
        sender.connect(G.Server.BOUNDED_ADDRESS);
        while (true) {
            int x = ByteBuffer.wrap(receiver.recv()).getInt();
            try {
                sleep(200);
            } catch (Exception e) {
            }
            System.out.println("[W " + n + "]: " + x);
            sender.send(ByteBuffer.allocate(4).putInt(x).array());
        }
    }
}
