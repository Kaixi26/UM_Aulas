package F;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.nio.ByteBuffer;

import static java.lang.Thread.sleep;

public class Server {

    static public final String BOUNDED_ADDRESS = "tcp://*:12345";
    static final String BOUNDED_ADDRESS_WORKERS = "inproc://workers";
    static private final int nWorkers = 5;

    public static void main(String[] args) throws Exception {
        final ZContext context = new ZContext();
        ZMQ.Socket receiver = context.createSocket(SocketType.PULL);
        receiver.bind(BOUNDED_ADDRESS);
        ZMQ.Socket workerSender = context.createSocket(SocketType.PUSH);
        workerSender.bind(BOUNDED_ADDRESS_WORKERS);

        for(int i=0; i<nWorkers; i++) {
            Thread worker = new Worker(context, i);
            worker.setDaemon(true);
            worker.start();
        }
        while(true){
            byte[] msg = receiver.recv();
            System.out.println(ByteBuffer.wrap(msg).getInt());
            workerSender.send(msg);
        }
    }

}
