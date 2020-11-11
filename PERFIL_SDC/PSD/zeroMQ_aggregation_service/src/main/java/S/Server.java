package S;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.nio.ByteBuffer;

public class Server {
    static public final String BOUNDED_ADDRESS = "tcp://*:12349";
    static public final String BOUNDED_ADDRESS_ANS = "tcp://*:12350";
    public static void main(String[] args) throws Exception {
        final ZContext context = new ZContext();
        ZMQ.Socket receiver = context.createSocket(SocketType.PULL);
        receiver.bind(BOUNDED_ADDRESS);
        ZMQ.Socket sender = context.createSocket(SocketType.PUSH);
        sender.bind(BOUNDED_ADDRESS_ANS);
        int acc = 0;
        while(true){
            int fgx = ByteBuffer.wrap(receiver.recv()).getInt();
            acc += fgx;
            System.out.println(acc);
            sender.send(ByteBuffer.allocate(4).putInt(acc).array());
        }
    }
}
